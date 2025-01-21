package utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class Criptografia {

    private static final String CHAVE_SECRETA = "1234567890123456"; 

    public static String criptografar(String cpf) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(CHAVE_SECRETA.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(cpf.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String descriptografar(String encryptedCpf) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(CHAVE_SECRETA.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedCpf));
        return new String(decrypted);
    }

}

