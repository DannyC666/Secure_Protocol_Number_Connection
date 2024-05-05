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
            delegatedClient.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        startClient(); 
    }
}
