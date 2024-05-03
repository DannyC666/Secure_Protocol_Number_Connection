package SecureAlgorithms;

import java.math.BigInteger;
import java.security.SecureRandom;

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
    public static BigInteger getSymmetricKey(BigInteger publicKey, BigInteger privateKey, BigInteger p) {
        return publicKey.modPow(privateKey, p);
    }
}
