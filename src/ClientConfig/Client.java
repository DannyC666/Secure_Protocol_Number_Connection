package ClientConfig;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Base64;

public class Client {

    public static final int PORT = 6666;
    public static final String SERVER = "localhost";
    public static PublicKey publicKey;
    private static ClientProtocol clientProtocol = new ClientProtocol();

    public  static  void main(String[] args) throws IOException {
        System.out.println("ClientConfig.Client starts");

        Socket socket = null;
        PrintWriter writer = null;
        BufferedReader reader = null;

        try {
            socket = new Socket(SERVER, PORT);
            // Writing to the socket
            writer = new PrintWriter(socket.getOutputStream(), true);
            // Reading from the socket
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            clientProtocol.processResponse(stdIn, reader, writer);

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


}