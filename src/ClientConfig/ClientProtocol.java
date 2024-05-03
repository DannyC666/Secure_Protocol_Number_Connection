package ClientConfig;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Base64;

public class ClientProtocol {
    public static PublicKey publicKey;
    public ClientProtocol(){

    }

    public  void processResponse(BufferedReader stdIn, BufferedReader pIn, PrintWriter pOut) {
        try {
            // Reads from terminal
            System.out.println("Write the message to send: ");
            String fromUser = stdIn.readLine();
            // Sends trough network
            pOut.println(fromUser);
            String fromServer;
            // Reads what is incoming from network
            // And if it's not null it prints it
            if ((fromServer = pIn.readLine()) != null) {
                String[] serverAnswer = fromServer.split(":");
                // Primera parte: Verificar comunicacion con el SV
                decodePublicKey(serverAnswer[2]);
                if(verfyRSAContent(serverAnswer[0], serverAnswer[1])){
                    pOut.println("OK");
                }else{
                    pOut.println("ERROR!");
                }
                // Segudna parte: Generar claves DH y verificarlas
                String[] serverGenDH = pIn.readLine().split(":");
                if(verifyDH(serverGenDH)){
                    pOut.println("OK DH");
                }else {
                    pOut.println("ERROR!");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean verfyRSAContent(String messageServerEncrypted, String messageServer) {
        boolean isServer = false;
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            byte[] decryptedMessage = cipher.doFinal(Base64.getDecoder().decode(messageServerEncrypted));
            isServer = new String(decryptedMessage).equals(messageServer);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            // Manejar las excepciones aquí
            e.printStackTrace(); // Puedes imprimir el mensaje de error o realizar otro tipo de manejo
        }
        return isServer;
}
    private static boolean verifyDH(String[] serverGenDH){
        boolean isServerAuth = false;
        String encryptG = serverGenDH[0];
        String encryptP = serverGenDH[1];
        String encryptGx =  serverGenDH[2];
        String G = serverGenDH[3];
        String P = serverGenDH[4];
        String Gx =  serverGenDH[5];
        if(verfyRSAContent(encryptG,G) && verfyRSAContent(encryptP,P)  && verfyRSAContent(encryptGx,Gx) ){
            isServerAuth = true;
        }
        return  isServerAuth;
    }

    private static void decodePublicKey(String publicKeyBase64){
        try {
            // Decodificar la cadena Base64 de la clave pública
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
            // Deserializar la clave pública
            ByteArrayInputStream bais = new ByteArrayInputStream(publicKeyBytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            publicKey= (PublicKey) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
