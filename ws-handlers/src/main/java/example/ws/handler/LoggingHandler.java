package example.ws.handler;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

/**
 * This SOAPHandler outputs the contents of inbound and outbound messages.
 */
public class LoggingHandler implements SOAPHandler<SOAPMessageContext> {

    public Set<QName> getHeaders() {
        return null;
    }

    public boolean handleMessage(SOAPMessageContext smc) {
        logToSystemOut(smc);
        return true;
    }

    public boolean handleFault(SOAPMessageContext smc) {
        logToSystemOut(smc);
        return true;
    }

    // nothing to clean up
    public void close(MessageContext messageContext) {
    }

    /**
     * Check the MESSAGE_OUTBOUND_PROPERTY in the context to see if this is an
     * outgoing or incoming message. Write a brief message to the print stream
     * and output the message. The writeTo() method can throw SOAPException or
     * IOException
     */
    private void logToSystemOut(SOAPMessageContext smc) {
        Boolean outbound = (Boolean) smc
                .get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (outbound) {
            System.out.println("Outbound SOAP message:");
        } else {
            System.out.println("Inbound SOAP message:");
        }

        SOAPMessage message = smc.getMessage();
        try {
            message.writeTo(System.out);
            System.out.println(); // just to add a newline to output
        } catch (Exception e) {
            System.out.printf("Exception in handler: %s%n", e);
        }
    }

}
