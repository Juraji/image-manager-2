package nl.juraji.imagemanager.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;

/**
 * Created by Juraji on 25-12-2018.
 * Image Manager 2
 */
public final class Crypt {
    private final byte[] cipherBytes;

    private Crypt(byte[] cipherBytes) {
        this.cipherBytes = cipherBytes;
    }

    public static Crypt init(byte[] cipherBytes) {
        return new Crypt(cipherBytes);
    }

    public static byte[] generateCipher(int length) {
        return new SecureRandom().generateSeed(length);
    }

    public byte[] encrypt(String value) throws GeneralSecurityException {
        if (value == null) {
            return new byte[]{};
        } else {
            final Key aesKey = new SecretKeySpec(cipherBytes, "AES");
            final Cipher cipher = Cipher.getInstance("AES");

            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            return cipher.doFinal(value.getBytes());
        }
    }

    public String decrypt(byte[] encryptedValue) throws GeneralSecurityException {
        if (encryptedValue == null || encryptedValue.length == 0) {
            return null;
        } else {
            final Key aesKey = new SecretKeySpec(cipherBytes, "AES");
            final Cipher cipher = Cipher.getInstance("AES");

            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            return new String(cipher.doFinal(encryptedValue));
        }
    }
}
