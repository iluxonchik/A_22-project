package pt.upa.broker;

import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.cli.BrokerClient;

public class BrokerClientApplication {

    public static void main(String[] args) throws Exception {
        System.out.println(BrokerClientApplication.class.getSimpleName() + " starting...");

        // TODO: DEMO REMOVE. This is a demo of a call to ping of the client, which itself includes demo code
        final String uddiURL = "http://localhost:9090";
        final String wsName = "UpaBroker";
        BrokerClient cli = new BrokerClient(uddiURL, wsName);
        cli.ping("Dr.Dre");
    }

}
