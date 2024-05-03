import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;

public class GeneratorIv {
    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }
    public static void main(String[] args) {
        IvParameterSpec iv = generateIv();
        System.out.println(generateIv());
        for (byte b : iv.getIV()) {
            System.out.format("%02x", b);
        }
    }
}
