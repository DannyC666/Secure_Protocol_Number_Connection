import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	private static final int PORT = 6666;
	
	public static void main(String[] args) throws IOException{
		
		ServerSocket serverSocket = null;
		System.out.println("*********Main server*********");
		
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		while(true) {
			Socket socket = serverSocket.accept();
			
			try {
				PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				serverProtocol(reader,writer);
				
				socket.close();
				writer.close();
				reader.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	private static void serverProtocol(BufferedReader readIn, PrintWriter writeOut) throws IOException {
		String inputLine;
		String outputLine;
		inputLine = readIn.readLine();
		System.out.println("Message client:" + inputLine);
		// TODO: Recibe el mensaje del cliente y lo cifra con la llave privada del servidor



		outputLine = "Connection stablished";
		writeOut.println(outputLine);
		System.out.println("Message answer: "+ outputLine );




	}
	
}
