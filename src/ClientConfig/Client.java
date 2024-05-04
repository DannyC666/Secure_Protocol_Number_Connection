package ClientConfig;

import java.io.*;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Scanner;

public class Client {

    public static final int PORT = 6666;
    public static final String SERVER = "localhost";
    public static PublicKey publicKey;

    public  static  void main(String[] args) throws IOException {
        System.out.println("ClientConfig.Client starts");
        
        Socket socket = null;

        try {
            socket = new Socket(SERVER, PORT);

            try ( // Crear un objeto Scanner para leer la entrada del usuario
            Scanner scanner = new Scanner(System.in)) {
                // Solicitar al usuario que ingrese un número
                System.out.println("Enter the number of clients to create: ");

                // Leer el número ingresado por el usuario
                int numberOfClients = scanner.nextInt();

                int idDelegatedClient = 0;
                while(idDelegatedClient < numberOfClients) {
                    DelegatedClient delegatedClient = new DelegatedClient(socket, idDelegatedClient);
                    idDelegatedClient++;
                    delegatedClient.start();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

}
