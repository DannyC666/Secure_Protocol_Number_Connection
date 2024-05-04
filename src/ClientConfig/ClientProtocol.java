package ClientConfig;

import SecureAlgorithms.DiffieHellman;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Objects;

public class ClientProtocol {
    public static PublicKey publicKey;
    private static BigInteger y; // Numero aleatorio privado local
    public static String P;
    public static String G;
    public static String Gx;
    private BigInteger symmetricKey;
    SecretKeySpec AESDataKey ;
    SecretKeySpec AESAuthKey;
    private static final DiffieHellman diffieHellman = new DiffieHellman();
    public ClientProtocol(){

    }

    public  void processResponse(BufferedReader stdIn, BufferedReader pIn, PrintWriter pOut) {
        try {
            // Reads from terminal
            System.out.println("Write the message to send: ");
            String fromUser = stdIn.readLine();
            // Sends trough network
            pOut.println(fromUser);
            // Recibir la llave publica del servidor
            String fromServer = pIn.readLine();
            String[] serverAnswer = fromServer.split(":");
            decodePublicKey(serverAnswer[2]);
            // Verofica que si sea el servidor el que aitentica
            if(verfyRSAContent(serverAnswer[0], serverAnswer[1])){
                    pOut.println("OK:ACK");
            }else{
                    pOut.println("ERROR!");
            }
                // Segunda parte: Generar claves DH y verificarlas
            String[] serverGenDH = pIn.readLine().split(":");
            if(verifyDH(serverGenDH)) {
                pOut.println("OK DH");
                String Gy = genLocalDiffieHellmanKey().toString();
                pOut.println(Gy);
                genSecretKeys();
                pIn.readLine();
                // Mandar seguramente el login y password
                System.out.println("Login:");
                fromUser = stdIn.readLine();
                String encryptionLogIn = diffieHellman.AESEncryptionAB1(fromUser,AESDataKey);
                pOut.println(encryptionLogIn);
                System.out.println("Password:");
                fromUser = stdIn.readLine();
                String encryptionPassword = diffieHellman.AESEncryptionAB1(fromUser,AESDataKey);
                pOut.println(encryptionPassword);
                    String flagServer =pIn.readLine();if (Objects.equals(flagServer, "OK Login")){System.out.println("Succesfully login");System.out.println("Enter a query:");fromUser = stdIn.readLine();// Send Query
                    String encryptionQuery = diffieHellman.AESEncryptionAB1(fromUser,AESDataKey);
                    String encryptionHMACQuery = diffieHellman.encryptHmac(fromUser,AESAuthKey);
                    pOut.println(encryptionQuery);
                    pOut.println(encryptionHMACQuery);
                    // ReceiveQuery
                    String repliedQuery = pIn.readLine();
                    String repliedQueryHASH = pIn.readLine();
                    //String repliedQuery = stdIn.readLine().split(":")[1];
                    if( verifyQuery(repliedQuery,repliedQueryHASH)){
                            System.out.println("Successful response:"+ diffieHellman.AESDecryptionAB1(repliedQuery,AESDataKey));
                    }}
            } else {
                    pOut.println("ERROR!");
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
    private  BigInteger genLocalDiffieHellmanKey(){
        y = diffieHellman.generateRandomPrivateKey();
        BigInteger bigIntegerP = new BigInteger(P);
        BigInteger bigIntegerG = new BigInteger(G);
        BigInteger Gy = diffieHellman.getGpowerXY(y,bigIntegerP,bigIntegerG);
        BigInteger bigIntegerGx = new BigInteger(Gx);
        this.symmetricKey = diffieHellman.getSymmetricKey(bigIntegerGx,y,bigIntegerP);
        return Gy;
    }

    private void genSecretKeys(){
        byte[][] partitionKey = diffieHellman.divideDHKey(symmetricKey);
        SecretKeySpec[] secretKeys= diffieHellman.makeSecureAESKeys(partitionKey);
        AESDataKey = secretKeys[0];
        AESAuthKey = secretKeys[1];
    }

    private void authLogInverfy(String login, String password){

    }

    private boolean verifyQuery(String queryEncrypted, String receivedQueryHMAC){
        boolean queryAccept = false;
        String decryptedQuery = diffieHellman.AESDecryptionAB1(queryEncrypted,AESDataKey);
        String localHMACQuery = diffieHellman.encryptHmac(decryptedQuery, AESAuthKey);
        if(Objects.equals(localHMACQuery, receivedQueryHMAC) && !Objects.equals(decryptedQuery, "Error Query")){
            queryAccept = true;
        }
        return  queryAccept;
    }

}
