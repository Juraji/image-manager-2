package nl.juraji.imagemanager.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;

/**
 * Created by Juraji on 15-12-2018.
 * Image Manager 2
 */
public final class EncryptionUtils {
    private EncryptionUtils() {
    }

    public static String randomString(int length) {
        final SecureRandom generator = new SecureRandom();
        final String characterSet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*";
        final StringBuilder result = new StringBuilder();
        int count = 0;

        while (count < length) {
            final char c = characterSet.charAt(generator.nextInt(characterSet.length()));
            result.append(c);
            ++count;
        }

        return result.toString();
    }

    public static byte[] encrypt(String cipherKey, String value) throws GeneralSecurityException {
        if (StringUtils.isEmpty(cipherKey)) {
            throw new IllegalArgumentException("cipher key can not be null or empty");
        }

        if (value == null) {
            return new byte[]{};
        } else {
            final Key aesKey = new SecretKeySpec(cipherKey.getBytes(), "AES");
            final Cipher cipher = Cipher.getInstance("AES");

            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            return cipher.doFinal(value.getBytes());
        }
    }

    public static String decrypt(String cipherKey, byte[] encryptedValue) throws GeneralSecurityException {
        if (StringUtils.isEmpty(cipherKey)) {
            throw new IllegalArgumentException("cipher key can not be null or empty");
        }


        if (encryptedValue == null) {
            return null;
        } else {
            final Key aesKey = new SecretKeySpec(cipherKey.getBytes(), "AES");
            final Cipher cipher = Cipher.getInstance("AES");

            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            return new String(cipher.doFinal(encryptedValue));
        }
    }
}
