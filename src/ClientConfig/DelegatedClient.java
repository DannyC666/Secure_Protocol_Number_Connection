package ClientConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class DelegatedClient extends Thread {
    private int idDelegatedClient;
    private Socket socket;
    private PrintWriter writer = null;
    private BufferedReader reader = null;
    private BufferedReader stdIn = null;

    public DelegatedClient(Socket socket, int idDelegatedClient) {
        this.socket = socket;
        this.idDelegatedClient = idDelegatedClient;
        this.stdIn = new BufferedReader(new InputStreamReader(System.in));
    }

    public void run() {
        System.out.println("New delegated client: " + idDelegatedClient);
        try {
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            ClientProtocol clientProtocol = new ClientProtocol();
            clientProtocol.processResponse(stdIn, reader, writer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
                if (writer != null) {
                    writer.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
