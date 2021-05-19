/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crypto;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author abhis
 */
public class AES {
 private static SecretKeySpec securityKey;
    private static byte[] keyByte;

    public static void setKey(String AESKey) {
        MessageDigest sha = null;// cryptographic hash table
        try {
            keyByte = AESKey.getBytes("UTF-8");// unicode transformation Format
            sha = MessageDigest.getInstance("SHA-1");// Secure Hash Algorithm
            keyByte = sha.digest(keyByte);
            keyByte = Arrays.copyOf(keyByte, 16);
            securityKey = new SecretKeySpec(keyByte, "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(String originalText, String secretKey) {
        try {
            setKey(secretKey);
            Cipher AEScipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            AEScipher.init(Cipher.ENCRYPT_MODE, securityKey);
            return Base64.getEncoder().encodeToString(AEScipher.doFinal(originalText.getBytes("UTF-8")));
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public static String decrypt(String encryptedText, String secretKey) {
        try {
            setKey(secretKey);
            Cipher AEScipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            AEScipher.init(Cipher.DECRYPT_MODE, securityKey);
            return new String(AEScipher.doFinal(Base64.getDecoder().decode(encryptedText)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
}
