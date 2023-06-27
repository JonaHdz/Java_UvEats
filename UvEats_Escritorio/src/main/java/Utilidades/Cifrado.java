package Utilidades;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Cifrado {
    public static String cifrarString (String cadena) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String clave = "clave_secreta";

        // Convertir la clave en una instancia de SecretKeySpec
        SecretKeySpec secretKeySpec = new SecretKeySpec(clave.getBytes(StandardCharsets.UTF_8), "AES");

        // Crear una instancia del cifrador AES
        Cipher cipher = Cipher.getInstance("AES");

        // Configurar el cifrador en modo de cifrado y con la clave secreta
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

        // Cifrar el mensaje
        byte[] mensajeCifrado = cipher.doFinal(cadena.getBytes(StandardCharsets.UTF_8));

        // Codificar el mensaje cifrado en Base64 para obtener una representación legible
        String mensajeCifradoBase64 = Base64.getEncoder().encodeToString(mensajeCifrado);

        System.out.println("Mensaje cifrado: " + mensajeCifradoBase64);
        return  mensajeCifradoBase64;
    }

}
