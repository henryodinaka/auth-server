package ng.min.authserve.utils;

import ng.min.authserve.execeptioins.MinServiceException;
import lombok.extern.slf4j.Slf4j;
import ng.min.authserve.constants.CommonConstant;
import ng.min.authserve.constants.ResponseCode;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Aes encryption
 */
@Component
@Slf4j
public class AES {

    private static SecretKeySpec secretKey;
    private static String encryptionKey;

    //    private static String decryptedString;
//    private static String encryptedString;
    @Value("${app.key.encryption.backend}")
    public void setEncryptionKey(String encryptionKey) {
        AES.encryptionKey = encryptionKey;
    }

    public static String getEncryptionKey() {
        return encryptionKey;
    }

    private static void setKey(String myKey) {


        MessageDigest sha;
        try {
            byte[] key = myKey.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // use only first 128 bit
            secretKey = new SecretKeySpec(key, "AES");


        } catch (NoSuchAlgorithmException e) {
            log.error("the error occured here while setting key ",e);
        }


    }

    //    public static String getDecryptedString() {
//        return decryptedString;
//    }
//    public static void setDecryptedString(String decryptedString) {
//        AES.decryptedString = decryptedString;
//    }
//    public static String getEncryptedString() {
//        return encryptedString;
//    }
//    public static void setEncryptedString(String encryptedString) {
//        AES.encryptedString = encryptedString;
//    }
    public static String encrypt(String strToEncrypt) {
        try {
//            log.info("Key used for Encryption {} string to encrypt {}",getEncryptionKey(),strToEncrypt);
            AES.setKey(getEncryptionKey());
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

            cipher.init(Cipher.ENCRYPT_MODE, secretKey);


            return Base64.encodeBase64String(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));


        } catch (Exception e) {

            log.error("Error while encrypting ", e);
        }
        return null;
    }

    public static String decrypt(String strToDecrypt) throws MinServiceException {
        try {
            if (!Validation.validData(strToDecrypt))
                throw new MinServiceException(ResponseCode.BAD_REQUEST.getCode(), ResponseCode.BAD_REQUEST.getValue().replace("{}", "Access token cannot be null. Pass it as header: " + CommonConstant.HEADER_STRING), ResponseCode.BAD_REQUEST.getStatusCode());
//           log.info("Key used for decryption {} string to decrypt {}",getEncryptionKey(),strToDecrypt);
            AES.setKey(getEncryptionKey());
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");

            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.decodeBase64(strToDecrypt)));

        } catch (IllegalArgumentException e) {

            log.error("IllegalArgumentException Error while decrypting JWT token: ", e);
            throw new MinServiceException(ResponseCode.BAD_REQUEST.getCode(),"Invalid token sent. Ensure you passed a valid token ",ResponseCode.BAD_REQUEST.getStatusCode());
        }catch (Exception e) {

            log.error("Error while decrypting: ", e);
        }
        return null;
    }

    public static void main(String[] args) {

    }


}