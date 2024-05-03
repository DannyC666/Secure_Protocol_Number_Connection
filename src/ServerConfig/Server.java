package ServerConfig;

import SecureAlgorithms.RSAFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Server {

	private static final int PORT = 6666;


    public Server(){
	}
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
		RSAFactory rsaFactory = new RSAFactory();
		rsaFactory.getKeys();
		PrivateKey privateKey =  rsaFactory.getPrivKey();
		PublicKey publicKey = rsaFactory.pubKey;

		ServerSocket serverSocket = null;
		System.out.println("*********Main server*********");
		
		try {
			serverSocket = new ServerSocket(PORT);


		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		int idDelegated = 1;
		while(true) {
			Socket socket = serverSocket.accept();
			DelegatedServer delegatedServer = new DelegatedServer(socket,idDelegated, privateKey,publicKey);
			idDelegated++;
			delegatedServer.start();
		}


	}





	
}
