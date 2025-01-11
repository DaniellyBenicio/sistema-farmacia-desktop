package utils;

import org.jasypt.util.text.AES256TextEncryptor;

public class Criptografia {

    private static final String SECRET_KEY = "CPF_ENCRYPTION_KEY";  
    
    public static String criptografar(String texto) {
        AES256TextEncryptor encryptor = new AES256TextEncryptor();
        encryptor.setPassword(SECRET_KEY);
        return encryptor.encrypt(texto);
    }

    public static String descriptografar(String textoCriptografado) {
        AES256TextEncryptor encryptor = new AES256TextEncryptor();
        encryptor.setPassword(SECRET_KEY);
        return encryptor.decrypt(textoCriptografado);
    }
}
