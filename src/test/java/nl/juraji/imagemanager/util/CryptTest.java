package nl.juraji.imagemanager.util;

import org.junit.jupiter.api.Test;

import java.security.GeneralSecurityException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by Juraji on 25-12-2018.
 * Image Manager 2
 */
class CryptTest {

    @Test
    public void cipherEncryptDecrypt() throws GeneralSecurityException {

        // Generate Cipher
        final byte[] cipher = Crypt.generateCipher(16);
        assertEquals(16, cipher.length);

        // Init Crypt
        final Crypt cryptInstance = Crypt.init(cipher);

        // Encrypt a string
        final String given = "ThisIsTheStringToEncrypt";
        final byte[] encryptedBytes = cryptInstance.encrypt(given);

        // Decrypt again and check
        final String decryptResult = cryptInstance.decrypt(encryptedBytes);
        assertEquals(given, decryptResult);
    }
}