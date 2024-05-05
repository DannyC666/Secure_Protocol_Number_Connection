package ServerConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;

public class DelegatedServer extends Thread {
    private final Socket socketClient;
    private final int idDelegatedSv;
    private ServerProtocol serverProtocol;

    public DelegatedServer(Socket socketClient, int idDelegatedSv, PrivateKey privateKey, PublicKey publicKey) {
        this.socketClient = socketClient;
        this.idDelegatedSv = idDelegatedSv;
        this.serverProtocol = new ServerProtocol(privateKey, publicKey);
    }

    public void run() {
        System.out.println("New delegated server: " + idDelegatedSv);
        try {
            PrintWriter writer = new PrintWriter(socketClient.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));

            serverProtocol.processMessage(reader, writer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socketClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
