package pt.upa.ca;


import pt.upa.ca.ws.cli.CAClient;

public class CAClientApplication {
    public static void main(String[] args) {
        System.out.println("Starting " + CAClientApplication.class.getSimpleName() + "...");
        try {
            CAClient.main(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
