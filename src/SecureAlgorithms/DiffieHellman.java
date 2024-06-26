package SecureAlgorithms;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class DiffieHellman {

    // Define the size of the keys in bits
    public static int keySizeInBits = 175;

    // Function to generate a random private key
    public  BigInteger generateRandomPrivateKey() {
        // Create a secure random number generator
        SecureRandom random = new SecureRandom();
        
        // Generate random bytes with the specified size
        byte[] randomBytes = new byte[keySizeInBits / 8];
        random.nextBytes(randomBytes);
        
        // Convert the random bytes to a BigInteger object
        BigInteger randomKey = new BigInteger(1, randomBytes);
        
        return randomKey;
    }

    // Function to calculate g^privateKey % p (public key generation)
    public  BigInteger getGpowerXY(BigInteger privateKey, BigInteger p, BigInteger g) {
        return g.modPow(privateKey, p);
    }

    // Function to calculate the symmetric key agreed upon by both parties
    public BigInteger getSymmetricKey(BigInteger publicKey, BigInteger privateKey, BigInteger p) {
        return publicKey.modPow(privateKey, p);
    }

    // Función para dividir la llave DH en clave de cifrado y clave para código HMAC
    public byte[][] divideDHKey(BigInteger symmetricKey) {
        byte[][] result = new byte[2][32];
        try {
            // Convertir la llave maestra a bytes
            byte[] symmetricKeyBytes = symmetricKey.toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] digest = md.digest(symmetricKeyBytes);
            // Dividir el digest en dos mitades (256 bits cada una)
            result[0] = Arrays.copyOfRange(digest, 0, 32); // Primeros 32 bytes (256 bits)
            result[1] = Arrays.copyOfRange(digest, 32, 64); // Siguientes 32 bytes (256 bits)

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }
    public SecretKeySpec[] makeSecureAESKeys(byte[][] byteKeys){
        SecretKeySpec[] symmetricKeys = new SecretKeySpec[2];
        byte[] datakey = byteKeys[0];
        byte[] MACKey = byteKeys[1];
        symmetricKeys[0] = new SecretKeySpec(datakey, "AES");
        symmetricKeys[1] = new SecretKeySpec(MACKey, "AES");
        return  symmetricKeys;
    }
    public String AESEncryptionAB1(String message, SecretKeySpec secretKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedMessage = cipher.doFinal(message.getBytes());
            return Base64.getEncoder().encodeToString(encryptedMessage);
        }catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
        e.printStackTrace();
        return null;
    }

    }
    public String AESDecryptionAB1(String encryptedMessage, SecretKeySpec secretKey) {
        try {
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedMessage);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        }catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            return null;
        }
    }
    public String encryptHmac(String message, SecretKey secretKey) {
        try {

            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKey);
            byte[] hmac = mac.doFinal(message.getBytes());
            return Base64.getEncoder().encodeToString(hmac);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
