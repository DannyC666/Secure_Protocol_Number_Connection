import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import javax.crypto.spec.DHParameterSpec;

public class Operations {
    public static int keySize = 175; // Tamaño de clave recomendado por OpenSSL

    public static KeyPair generateDHKeyPair(int keySize) {
        try {
            // Obtener el generador de pares de claves para Diffie-Hellman
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH");

            // Inicializar el generador con el tamaño de clave deseado
            keyGen.initialize(keySize);

            // Generar el par de claves
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Algorithm not available: " + e.getMessage());
            return null;
        } catch (InvalidAlgorithmParameterException e) {
            System.err.println("Invalid parameters: " + e.getMessage());
            return null;
        }
    }


    
}