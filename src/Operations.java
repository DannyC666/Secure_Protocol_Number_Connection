import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;


public class Operations {
    public static int keySize = 175; // Tamaño de clave recomendado por OpenSSL

    public static PrivateKey generateDHKeyPair(int keySize) {
        try {
            // Obtener el generador de pares de claves para Diffie-Hellman
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DiffieHelman");

            // Inicializar el generador con el tamaño de clave deseado
            keyGen.initialize(keySize);

            // Generar el par de claves
            return keyGen.generateKeyPair().getPrivate();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Algorithm not available: " + e.getMessage());
            return null;
        }
    }

}