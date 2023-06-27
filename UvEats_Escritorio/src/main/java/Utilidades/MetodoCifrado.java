package Utilidades;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class MetodoCifrado {
    private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY = "mySecretKey12345"; // Clave secreta de 16 caracteres (128 bits)

    public static String encriptarCadena(String plainText)  {
        byte[] encryptedBytes = new byte[0];
        try{
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            encryptedBytes = cipher.doFinal(plainText.getBytes());

        }catch (Exception e){

        }
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String desencriptarCadena(String encryptedText)  {
        byte[] decryptedBytes = new byte[0];
        try{
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
             decryptedBytes = cipher.doFinal(decodedBytes);
        }catch (Exception e){
            
        }
        
        return new String(decryptedBytes);
    }

}
