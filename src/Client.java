import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    public static final int PORT = 6666;
    public static final String SERVER = "localhost";
    
    public static void main(String[] args) throws IOException {
        System.out.println("Client starts");
        
        Socket socket = null;
        PrintWriter writer = null;
        BufferedReader reader = null;

        try {
            socket = new Socket(SERVER, PORT);
            // Writing to the socket
            writer = new PrintWriter(socket.getOutputStream(), true);
            // Reading from the socket
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            procesar(new BufferedReader(new InputStreamReader(System.in)), reader, writer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
    }

    public static void procesar(BufferedReader stdIn, BufferedReader pIn, PrintWriter pOut) throws IOException {
        // Reads from terminal
        System.out.println("Write the message to send: ");
        String fromUser = stdIn.readLine();

        // Sends trough network
        pOut.println(fromUser);

        String fromServer = "";

        // Reads what is incoming from network
        // And if it's not null it prints it
        if ((fromServer = pIn.readLine()) != null) {
            System.out.println("Server answer: " + fromServer);
        }
    }
}
