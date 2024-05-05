package ClientConfig;

import java.io.IOException;
import java.net.Socket;

public class Client {
    public static final int PORT = 6666;
    public static final String SERVER = "localhost";

    public static void main(String[] args) {
        System.out.println("ClientConfig.Client starts");
        try {
            Socket socket = new Socket(SERVER, PORT);
            DelegatedClient delegatedClient = new DelegatedClient(socket, PORT);
            delegatedClient.start();
            delegatedClient.join(); // Espera a que el hilo termine si deseas manejarlo así
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
