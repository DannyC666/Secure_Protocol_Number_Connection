package SecureAlgorithms;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

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
    public static BigInteger getGpowerXY(BigInteger privateKey, BigInteger p, BigInteger g) {
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

            // Crear un objeto MessageDigest para SHA-512
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            // Calcular el digest
            byte[] digest = md.digest(symmetricKeyBytes);

            // Dividir el digest en dos mitades (256 bits cada una)
            result[0] = Arrays.copyOfRange(digest, 0, 32); // Primeros 32 bytes (256 bits)
            result[1] = Arrays.copyOfRange(digest, 32, 64); // Siguientes 32 bytes (256 bits)

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return result;
    }
}
