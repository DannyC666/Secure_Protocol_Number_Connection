package ServerConfig;

import SecureAlgorithms.RSAFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 6666;
    private static final int THREAD_COUNT = 10;

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        RSAFactory rsaFactory = new RSAFactory();
        rsaFactory.getKeys();
        PrivateKey privateKey = rsaFactory.getPrivKey();
        PublicKey publicKey = rsaFactory.pubKey;

        System.out.println("*********Main server*********");
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            int idDelegated = 1;
            while (true) {
                Socket socket = serverSocket.accept();
                DelegatedServer delegatedServer = new DelegatedServer(socket, idDelegated++, privateKey, publicKey);
                executor.execute(delegatedServer);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        } finally {
            executor.shutdown();
        }
    }
}
