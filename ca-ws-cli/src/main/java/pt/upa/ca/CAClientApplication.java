package pt.upa.ca;


import pt.upa.ca.ws.cli.CAClient;

public class CAClientApplication {
    public static void main(String[] args) {
        System.out.println("Starting " + CAClientApplication.class.getSimpleName() + "...");

        if (args.length < 2) {
            System.err.println("Argument(s) missing!");
            System.err.printf("Usage: java %s uddiURL name%n", CAClient.class.getName());
            return;
        }

        final String uddiUrl = args[0];
        final String wsName = args[1];
        CAClient caClient = new CAClient(uddiUrl, wsName);

        try {
            System.out.println(caClient.ping("Dr.Dre"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
