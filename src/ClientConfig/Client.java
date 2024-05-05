package ClientConfig;

import java.net.Socket;

public class Client {
    public static final int PORT = 6666;
    public static final String SERVER = "localhost";

    public static void startClient() {
        System.out.println("ClientConfig.Client starts");
        try {
            Socket socket = new Socket(SERVER, PORT);
            DelegatedClient delegatedClient = new DelegatedClient(socket, PORT);
            delegatedClient.start();
            // Si necesitas que el cliente principal espere a que todos terminen, puedes usar join() aquí,
            // pero esto hará que cada cliente deba terminar antes de que el siguiente pueda comenzar.
            delegatedClient.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        startClient(); // Mantiene la capacidad de correr un solo cliente si se desea.
    }
}
