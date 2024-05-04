package SecureAlgorithms;

import java.security.*;

public class RSAFactory {

    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    public PublicKey pubKey ;
    private PrivateKey privKey;

    public RSAFactory() throws NoSuchAlgorithmException {
    }
    public void getKeys()  {
       keyPairGenerator.initialize(4096);
       KeyPair keyPair = keyPairGenerator.generateKeyPair();
       System.out.println("Algoritmo de cifrado usado: "+keyPairGenerator.getAlgorithm());
       PublicKey publicKey = keyPair.getPublic() ;
       PrivateKey privateKey = keyPair.getPrivate();
       pubKey = publicKey;
       privKey = privateKey;
   }
    public PrivateKey getPrivKey() {
        return privKey;
    }
}

