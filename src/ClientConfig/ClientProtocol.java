package ClientConfig;

import SecureAlgorithms.DiffieHellman;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Base64;

public class ClientProtocol {
    public static PublicKey publicKey;
    private static BigInteger y; // Numero aleatorio privado local
    public static String P;
    public static String G;
    public static String Gx;
    private static BigInteger symmetricKey;
    private static BigInteger authCode;
    private static final DiffieHellman diffieHellman = new DiffieHellman();
    private static BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

    public static  void processResponse(BufferedReader pIn, PrintWriter pOut) {
        try {
            // Reads from terminal
            System.out.println("Write the message to send: ");
            String fromUser = stdIn.readLine();
            // Sends trough network
            pOut.println(fromUser);
            String fromServer;
            // Reads what is incoming from network and if it's not null it prints it
            if ((fromServer = pIn.readLine()) != null) {
                String[] serverAnswer = fromServer.split(":");

                // Primera parte: Verificar comunicacion con el SV
                decodePublicKey(serverAnswer[2]);
                if(verfyRSAContent(serverAnswer[0], serverAnswer[1])){
                    pOut.println("OK");
                }else{
                    pOut.println("ERROR!");
                }

                // Segunda parte: Generar claves DH y verificarlas
                String[] serverGenDH = pIn.readLine().split(":");
                if(verifyDH(serverGenDH)) {
                    pOut.println("OK DH");
                    String Gy = genLocalDiffieHellmanKey().toString();
                    pOut.println(Gy);

                    // Third part: divide DH key in symmetric key and verification code
                    byte[][] partitionKey = diffieHellman.divideDHKey(symmetricKey);

                    // Convertir el primer elemento a BigInteger y guardarlo en symmetricKey
                    symmetricKey = new BigInteger(partitionKey[0]);

                    // Convertir el segundo elemento a BigInteger y guardarlo en authCode
                    authCode = new BigInteger(partitionKey[1]);

                    System.out.println("symmetricKey: " + symmetricKey);

                    System.out.println();

                    System.out.println("authCode: " + authCode);

                } else {
                    pOut.println("ERROR!");
                }
                // Starts login in continue  :)
                System.out.println(pIn.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BigInteger getSymmetricKey() {
        return symmetricKey;
    }

    public static void setSymmetricKey(BigInteger symmetricKey) {
        ClientProtocol.symmetricKey = symmetricKey;
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
            e.printStackTrace();
        }
        return isServer;
    }
    private static boolean verifyDH(String[] serverGenDH){
        boolean isServerAuth = false;
        String encryptG = serverGenDH[0];
        String encryptP = serverGenDH[1];
        String encryptGx =  serverGenDH[2];
        G = serverGenDH[3];
        P = serverGenDH[4];
        Gx =  serverGenDH[5];
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
    private static  BigInteger genLocalDiffieHellmanKey(){
        y = diffieHellman.generateRandomPrivateKey();
        BigInteger bigIntegerP = new BigInteger(P);
        BigInteger bigIntegerG = new BigInteger(G);
        BigInteger Gy = diffieHellman.getGpowerXY(y,bigIntegerP,bigIntegerG);
        BigInteger bigIntegerGx = new BigInteger(Gx);
        BigInteger symmetricKey = diffieHellman.getSymmetricKey(bigIntegerGx,y,bigIntegerP);
        setSymmetricKey(bigIntegerGx);
        System.out.println("DH key: " + symmetricKey);
        return Gy;
    }
}
