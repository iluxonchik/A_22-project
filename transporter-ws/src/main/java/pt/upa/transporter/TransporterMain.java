package pt.upa.transporter;

import javax.xml.ws.Endpoint;

public class TransporterMain {
	public static void main(String[] args) {
		if (args.length < 3) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL wsName wsURL%n", TransporterMain.class.getName());
			return;
		}
		
		String uddiURL = args[0];
		String name = args[1];
		String url = args[2];
		
		Endpoint endpoint;
		UDDINaming uddiNaming;
		
	}
	
}
