package pt.upa.handler;


import pt.upa.ca.ws.cli.CAClient;
import pt.upa.shared.domain.CertificateHelper;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pt.upa.handler.RequestIDHandler.CONTEXT_REQUEST_ID;

public class AuthenticationHandler implements SOAPHandler<SOAPMessageContext> {
    /**
     * {H(NONCE | BODY)}Ks
     */
    public static final String CONTEXT_SENDER_NAME = "pt.upa.handler.senderName";
    public static final String CONTEXT_PRIVATE_KEY = "pt.upa.handler.privateKey";

    private static final String SENDER_HEADER_NAME = "pt.upa.handler.senderName";
    private static final String NONCE_HEADER_NAME = "pt.upa.handler.nonce";
    private static final String AUTH_HEADER_NAME = "pt.upa.handler.auth";

    private static final String NAME_PREFIX = "d";
    private static final String NAMESPACE = "http://pt.upa.a22";

    private static boolean DEBUG = false;
    private final Pattern pattern = Pattern.compile("<S:Body>.*<:Body>");

    @Override
    public Set<QName> getHeaders() {
        return null;
    }

    @Override
    public boolean handleMessage(SOAPMessageContext soapMessageContext) {
        setDebug(true);
        boolean isOutbound = (Boolean) soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        try {
            // get SOAP envelope
            final SOAPMessage msg = soapMessageContext.getMessage();
            final SOAPPart sp = msg.getSOAPPart();
            final SOAPEnvelope senv = sp.getEnvelope();
            SOAPHeader sh = senv.getHeader();

            final String senderName;
            final PrivateKey privateKey;
            final long nonce = getNonce();


            if (isOutbound) {
                // Outbound message. let's sign it
                log("Handling OUTBOUND message");
                final String msgToAuth = nonce + getSoapBodyXML(msg);

                if (!soapMessageContext.containsKey(CONTEXT_SENDER_NAME)) {
                    // if the request ID was not passed, don't send it in the header
                    log("[FALSE] Sender Name was not provided in context");
                    return false;
                }
                senderName = (String) soapMessageContext.get(CONTEXT_SENDER_NAME);

                if (!soapMessageContext.containsKey(CONTEXT_PRIVATE_KEY)) {
                    // if the request ID was not passed, don't send it in the header
                    log("[FALSE] PrivateKey name was not provided in context");
                    return false;
                }
                privateKey = (PrivateKey) soapMessageContext.get(CONTEXT_PRIVATE_KEY);

                // Let's sign and encode the message
                byte[] signature = CertificateHelper.makeDigitalSignature(msgToAuth, privateKey);
                String encodedSig = Base64.getEncoder().encodeToString(signature);

                // add a header if needed
                if (sh == null) {
                    sh = senv.addHeader();
                }

                // add the needed elements to header and we're good to go!
                addHeaderElement(sh, senv, SENDER_HEADER_NAME, senderName);
                addHeaderElement(sh, senv, NONCE_HEADER_NAME, String.valueOf(nonce));
                addHeaderElement(sh, senv, AUTH_HEADER_NAME, encodedSig);

                log("[IMPORTANT] OUTBOUND Nonce : Encoded " + nonce + " : " + encodedSig);
                log("[IMPORTANT] OUTBOUND Nonce : BODY " + nonce + " : " + getSoapBodyXML(msg));


            } else {
                // Inbound message
                // The first thing to do is to check whether the header has all of the expected elements or Nah. In the
                // latter case, return false
                log("Handling INBOUND message");
                HashMap<String, String> headerElems = new HashMap<>();

                parseHeaderElements(sh, senv, headerElems);

                // Contact CA for certificate
                // TODO: check local cache first
                CAClient ca = new CAClient();
                Certificate cert  = CertificateHelper
                        .readCertificateFromByteArray(ca.getCertificate(headerElems.get(SENDER_HEADER_NAME)));
                log("Got certificate from CA");
                log("[IMPORTANT] INBOUND Nonce : Encoded " + nonce + " : " + headerElems.get(AUTH_HEADER_NAME));
                log("[IMPORTANT] INBOUND Nonce : BODY " + nonce + " : " + getSoapBodyXML(msg));

                byte [] digest = Base64.getDecoder().decode(headerElems.get(AUTH_HEADER_NAME));
                String message = headerElems.get(NONCE_HEADER_NAME) + getSoapBodyXML(msg);
                boolean res = CertificateHelper.verifyDigitalSignature(digest, message.getBytes(), cert.getPublicKey());
                if (res) {
                    log("[SUCCESS] Signature OK");
                    return true;
                } else {
                    log("[FAIL] Signature FAIL");
                    return false;
                }

            }

        } catch (Exception e) {
            log("[FALSE | CRITICAL] Error in handleMessage()");
            log(e.getMessage());
            return false;
        }

        return true;
    }

    private void parseHeaderElements(SOAPHeader sh, SOAPEnvelope senv, HashMap<String, String> elems) throws
            SOAPException, HeaderElementMissingExcepiton {

        if (sh == null) {
            log("[FALSE] Header not found");
            throw new HeaderElementMissingExcepiton("Header missing altogether");
        }

        parseHeaderElement(sh, senv, SENDER_HEADER_NAME, elems);
        parseHeaderElement(sh, senv, NONCE_HEADER_NAME, elems);
        parseHeaderElement(sh, senv, AUTH_HEADER_NAME, elems);
    }

    private void parseHeaderElement(SOAPHeader sh, SOAPEnvelope senv, String elemName, HashMap<String, String> elems)
            throws SOAPException, HeaderElementMissingExcepiton {

        Name name = senv.createName(elemName, NAME_PREFIX, NAMESPACE);
        Iterator it = sh.getChildElements(name);

        if (!it.hasNext()) {
            log("[FALSE] " + elemName + " not in header");
            throw new HeaderElementMissingExcepiton(elemName + " missing");
        }

        elems.put(elemName, ((SOAPElement) it.next()).getValue());
        log("Sender Name: " + elems.get(elemName));
    }

    @Override
    public boolean handleFault(SOAPMessageContext soapMessageContext) {
        return false;
    }

    @Override
    public void close(MessageContext messageContext) {

    }

    private final String getSoapBodyXML(SOAPMessage msg) throws IOException, SOAPException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        msg.writeTo(out);
        String result = new String(out.toByteArray());
        //return new String(result);
        return "";
    }

    private long getNonce() {
        return System.currentTimeMillis();
    }

    private void addHeaderElement(SOAPHeader sh, SOAPEnvelope senv, String elemName, String elemValue) throws SOAPException {
        // add header element (name, namespace prefix, namespace)
        Name name = senv.createName(elemName, NAME_PREFIX, NAMESPACE);
        SOAPHeaderElement element = sh.addHeaderElement(name);

        // add header element value
        element.addTextNode(elemValue);
        log("[HEADER_ADD] Added element <" + elemName + "> : <" + elemValue + "> to header");
    }

    public static void setDebug(boolean value) {
        DEBUG = value;
    }

    public static boolean getDebug() {
        return DEBUG;
    }

    // because loggers are too mainstream
    public void log(String msg) {
        if (DEBUG) {
            System.out.println("AuthenticationHandler: " + msg);
        }
    }
}
