package pt.upa.handler;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.Iterator;
import java.util.Set;

public class RequestIDHandler implements SOAPHandler<SOAPMessageContext> {
    private static final String headerName = "pt.upa.handler.requestID";
    private static final String namePrefix = "d";
    private static final String namespace = "http://pt.upa.a22";

    public static final String CONTEXT_REQUEST_ID = "pt.upa.handler.requestID";
    private static boolean DEBUG = true;

    @Override
    public Set<QName> getHeaders() {
        return null;
    }

    @Override
    public boolean handleMessage(SOAPMessageContext soapMessageContext) {
        System.out.println("RequestIDHandler: Handling OUTBOUND message");

        boolean isOutbound = (Boolean) soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        try {
            // get SOAP envelope
            SOAPMessage msg = soapMessageContext.getMessage();
            SOAPPart sp = msg.getSOAPPart();
            SOAPEnvelope senv = sp.getEnvelope();
            SOAPHeader sh = senv.getHeader();

            if (isOutbound) {
                // Outbound message, so get the request ID from SOAPMessageContext and add it to the header

                if (!soapMessageContext.containsKey(CONTEXT_REQUEST_ID)) {
                    // if the request ID was not passed, don't send it in the header
                    log("RequestIDHandler: No requestID was passed, skipping HEADER modification");
                    return true;
                }

                String requestID = (String) soapMessageContext.get(CONTEXT_REQUEST_ID);

                // add a header if needed
                if (sh == null) {
                    sh = senv.addHeader();
                }

                // add header element (name, namespace prefix, namespace)
                Name name = senv.createName(headerName, namePrefix, namespace);
                SOAPHeaderElement element = sh.addHeaderElement(name);

                // add header element value
                element.addTextNode(requestID);
                log("RequestIDHandler: Added requestID: \"" + requestID + "\"");

            } else {
                // Inbound message, so what we'll want to do is get the requestID value form the HEADER and put it in
                // the SOAPMessageContext, so that it can be read on domain/service side
                log("RequestIDHandler: Handling INBOUND message");

                if (sh == null) {
                    log("RequestIDHandler: Header not found, skipping SOAPMessage inflation");
                    return true;
                }

                // read the value from the header
                Name name = senv.createName(headerName, namePrefix, namespace);
                Iterator it = sh.getChildElements(name);

                if (!it.hasNext()) {
                    log("RequestIDHandler: requestID HEADER element not found, skipping SOAPMessage " +
                            "inflation");
                    return true;
                }
                SOAPElement element = (SOAPElement) it.next();

                String requestIDValue = element.getValue();
                log("RequestIDHandler: requestID value received: \"" + requestIDValue + "\"");

                soapMessageContext.put(CONTEXT_REQUEST_ID, requestIDValue);
                // make the property visible to application so that it can be accessed by the application
                soapMessageContext.setScope(CONTEXT_REQUEST_ID, MessageContext.Scope.APPLICATION);
            }

        } catch (Exception e) {
            log("Error in RequestIDHandler.handleMessage");
            log(e.getMessage());
            log("It's all good though, continuing normal processing...");

        }

        return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext soapMessageContext) {
        return false;
    }

    @Override
    public void close(MessageContext messageContext) {

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
            System.out.println(msg);
        }
    }
}
