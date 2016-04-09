package pt.upa.transporter;

import pt.upa.transporter.ws.cli.TransporterClient;

public class TransporterClientApplication {

	public static void main(String[] args) throws Exception {
		System.out.println(TransporterClientApplication.class.getSimpleName() + " starting...");

        // TODO: placeholder demo client call
        TransporterClient cli = new TransporterClient("http://localhost:9090", "UpaTransporter1");
        cli.ping("NullPointerException");
	}
}
