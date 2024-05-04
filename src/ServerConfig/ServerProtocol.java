package ServerConfig;

import SecureAlgorithms.DiffieHellman;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Objects;

public class ServerProtocol {
    public static String pDiffieHelman = "00:80:80:84:ff:3e:5b:97:7c:32:52:59:32:77:5d:" +
            "ea:78:21:1e:dd:01:4c:8a:48:9b:c9:87:47:25:69:" +
            "ea:c6:a5:7c:87:1f:2c:e9:86:16:ad:86:97:f2:fe:" +
            "63:e8:ec:df:be:59:86:32:32:d7:3c:18:20:b5:4f:" +
            "21:cd:7d:e2:fe:f9:da:47:0e:c2:32:3a:f7:41:3c:" +
            "c6:c3:43:ce:88:13:b5:c3:17:11:43:0a:b0:65:4f:" +
            "b8:2f:4d:76:bb:e6:39:8d:2c:c7:2a:3d:91:d4:97:" +
            "9e:62:2f:20:45:bb:d6:26:d8:71:52:3a:83:4a:88:" +
            "b7:c0:e9:05:9e:94:af:f1:c7";


    public static PublicKey publicKey;
    private static PrivateKey privateKey;
    private BigInteger symmetricKey;
    SecretKeySpec AESDataKey;
    SecretKeySpec AESAuthKey;
    public static final  BigInteger P = new BigInteger(pDiffieHelman.replaceAll(":", ""), 16);
    public static final int G = 2;
    private static final DiffieHellman  diffieHellman = new DiffieHellman();
    private static  BigInteger x; // Numero aleatorio privado local


    public ServerProtocol(PrivateKey privateKey ,PublicKey publicKey ) {
        ServerProtocol.privateKey = privateKey;
        ServerProtocol.publicKey = publicKey;
        x = diffieHellman.generateRandomPrivateKey();
    }

    public  void processMessage(BufferedReader readIn, PrintWriter writeOut) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String inputLine;
        // 3. Server receives a SYN request form client and reply
        inputLine = readIn.readLine();
        System.out.println("Message client:" + inputLine);
        String encryptedMessage = encryptMessages(inputLine);
        String publicKeySerialized = serializeKeys(publicKey);
        writeOut.println(encryptedMessage+":"+inputLine+":"+publicKeySerialized);
        inputLine = readIn.readLine();
        System.out.println("Client ACK confirm: "+ inputLine);

        // 6-7. Server generate and send to client DiffieHellman params
        String symmetricPack = genDHParams();
        writeOut.println(symmetricPack);
        inputLine = readIn.readLine();
        System.out.println("Client DiffieHellman confirm: "+ inputLine);

        // 11.1 Server receive Gy and  11.2 agenerate MACKey(AB_2) and AESKey(AB_1)
        BigInteger Gy = new BigInteger(readIn.readLine());
        genLocalDiffieHellmanKey(Gy);
        writeOut.println("Continue :)");
        genSecretKeys();

        // 15. Receive and verify login-password from Client
        String login = readIn.readLine();
        String password = readIn.readLine();
        System.out.println("Received Login:" + diffieHellman.AESDecryptionAB1(login,AESDataKey));
        System.out.println("Received Password:" + diffieHellman.AESDecryptionAB1(password,AESDataKey));
        writeOut.println("OK Login");

        // 19-20. Receive and verify query
        String queryAB1 = readIn.readLine();
        String queryHMAC = readIn.readLine();
        String queryMessage = replyQuery(queryAB1, queryHMAC);
        String encryptionQueryMessage = diffieHellman.AESEncryptionAB1(queryMessage,AESDataKey);
        String encryptionHMACQueryMessage = diffieHellman.encryptHmac(queryMessage,AESAuthKey);
        writeOut.println(encryptionQueryMessage);
        writeOut.println(encryptionHMACQueryMessage);

    }

    private static String encryptMessages(String message) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] encryptedMessage = cipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(encryptedMessage);
    }

    private static String serializeKeys(PublicKey publicKey) throws IOException {
        // Serializar la clave pública
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(publicKey);
        oos.close();
        byte[] publicKeyBytes = baos.toByteArray();
        return Base64.getEncoder().encodeToString(publicKeyBytes);

    }

    private String genDHParams() throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        BigInteger gx = diffieHellman.getGpowerXY(x, P, BigInteger.valueOf(G));
        String packetParams = G+":"+P+":"+ gx;
        String encryptG = encryptMessages(String.valueOf(G));
        String encryptP = encryptMessages(String.valueOf(P));
        String encryptGx = encryptMessages(String.valueOf(gx));
        return encryptG+":"+encryptP+":"+encryptGx+":"+packetParams;
    }

    private void genLocalDiffieHellmanKey(BigInteger Gy){
        this.symmetricKey = diffieHellman.getSymmetricKey(Gy,x,P);
    }

    private void genSecretKeys(){
        byte[][] partitionKey = diffieHellman.divideDHKey(symmetricKey);
        SecretKeySpec[] secretKeys= diffieHellman.makeSecureAESKeys(partitionKey);
        AESDataKey = secretKeys[0];
        AESAuthKey = secretKeys[1];
    }

    private String replyQuery(String queryEncrypted, String receivedQueryHMAC){
        String responseQuery = "Error Query";
        String decyptedQuery = diffieHellman.AESDecryptionAB1(queryEncrypted,AESDataKey);
        String localHMACQuery = diffieHellman.encryptHmac(decyptedQuery, AESAuthKey);
        if(Objects.equals(localHMACQuery, receivedQueryHMAC)){
            System.out.println("Received Query:"+ decyptedQuery);
            responseQuery = "Example response to "+ decyptedQuery;
        }
        return  responseQuery;
    }

}
