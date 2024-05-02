import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class RSAFactory {

    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");

    public RSAFactory() throws NoSuchAlgorithmException {

        keyPairGenerator.initialize(2048);

        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        System.out.println("Algoritmo de cifrado usado: "+keyPairGenerator.getAlgorithm());
        String publicKey = keyPair.getPublic().toString();
        String privateKey = keyPair.getPrivate().toString();

        System.out.println("Clave Pública:");
        System.out.println(publicKey);
        System.out.println("\nClave Privada:");
        System.out.println(privateKey);

    }
}
