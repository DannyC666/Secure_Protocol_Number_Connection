import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ClientConfig.Client;
import ServerConfig.Server;

public class Main {
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Starting the server...");
        Thread serverThread = new Thread(() -> {
            try {
                Server.main(new String[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serverThread.start();

        // Espera para asegurar que el servidor esté activo
        Thread.sleep(1000);
        System.out.print("Hello and welcome! How many delegated clients would you like to create?: ");
        int numberOfClients = Integer.parseInt(reader.readLine());

        ExecutorService executor = Executors.newFixedThreadPool(numberOfClients); // Crear un pool de hilos

        for (int i = 0; i < numberOfClients; i++) {
            int finalI = i;
            executor.submit(() -> {
                System.out.println("Starting client " + (finalI + 1));
                Client.startClient();
            });
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS); // Espera que todos los clientes terminen
    }
}
