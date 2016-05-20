package pt.upa.transporter;

import pt.upa.transporter.ws.cli.TransporterClient;

public class TransporterClientApplication {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Running Demo 3");
            runDemo3();
        } else {
            if (args[0].equals("1")) {
                System.out.println("Running Demo 1");
                runDemo1();
            } else if (args[0].equals("3")) {
                System.out.println("Running Demo 3");
                runDemo3();
            }
            else {
                System.out.println("Running Demo 2");
                runDemo2();
            }
        }
    }

    /**
     * Demo 2: replay attack (send msg with same nonce twice)
     */
    private static void runDemo2() {
        System.out.println(TransporterClientApplication.class.getSimpleName() + " starting...");

        TransporterClient cli = new TransporterClient("http://localhost:9090/", "UpaTransporter1");
        cli.DEMOFixNonce("123");
        System.out.println(cli.ping("World"));
        System.out.println(cli.ping("World"));
    }

    /**
      *DEMO 1: Change message without changing the digest
     */
    public static void runDemo1() {
        System.out.println(TransporterClientApplication.class.getSimpleName() + " starting...");

        TransporterClient cli = new TransporterClient("http://localhost:9090/", "UpaTransporter1");
        cli.DEMOchangeNonce("123");
        System.out.println(cli.ping("MITM"));
    }

    static void runDemo3() {
        System.out.println(TransporterClientApplication.class.getSimpleName() + " starting...");

        TransporterClient cli = new TransporterClient("http://localhost:9090/", "UpaTransporter1");
        cli.DEMOturnDemoModeOn();
        System.out.println(cli.ping("World"));
    }
}
