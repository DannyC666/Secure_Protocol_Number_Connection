package ClientConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class DelegatedClient extends Thread {
    private int idDelegatedClient;
    private Socket socket;
    PrintWriter writer = null;
    BufferedReader reader = null;
    BufferedReader stdIn = null;

    public DelegatedClient(Socket socket, int idDelegatedClient) {
        this.socket = socket;
        this.idDelegatedClient = idDelegatedClient;
    }
 
    public void run(){
        System.out.println("New delegated client: " + idDelegatedClient);

        try {
            // Writing to the socket
            writer = new PrintWriter(socket.getOutputStream(), true);
            // Reading from the socket
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // stdIn = new BufferedReader(new InputStreamReader(System.in));

            //ClientProtocol.processResponse(reader, writer);

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (writer != null) {
                writer.close();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

}
