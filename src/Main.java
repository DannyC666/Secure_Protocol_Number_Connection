import java.io.BufferedReader;
import java.io.InputStreamReader;

import ClientConfig.Client;
import ServerConfig.Server; 

public class Main {
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        
        // Inicia el servidor en un hilo separado para que pueda comenzar a escuchar conexiones
        System.out.println("Starting the server...");
        Thread serverThread = new Thread(() -> {
            try {
                Server.main(new String[0]); 
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serverThread.start();

        // Pequeña pausa para asegurar que el servidor está corriendo antes de iniciar clientes
        Thread.sleep(1000); // Ajusta este tiempo según sea necesario

        System.out.println("Hello and welcome! How many delegated clients would you like to create?");
        int numberOfClients = Integer.parseInt(reader.readLine());

        for (int i = 0; i < numberOfClients; i++) {
            System.out.println("Starting client " + (i + 1));
            Client.startClient();
            Thread.sleep(500); // Espacio entre clientes para evitar sobrecarga instantánea en el servidor
        }
    }
}
