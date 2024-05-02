import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class RSAFactory {

    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");

    public RSAFactory() throws NoSuchAlgorithmException {

        keyPairGenerator.initialize(2048);

        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        String publicKey = keyPair.getPublic().toString();
        String privateKey = keyPair.getPrivate().toString();

        System.out.println("Clave Pública:");
        System.out.println(publicKey);
        System.out.println("\nClave Privada:");

    }
}
