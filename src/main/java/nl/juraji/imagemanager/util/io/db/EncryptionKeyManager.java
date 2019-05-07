package nl.juraji.imagemanager.util.io.db;

import io.ebean.config.EncryptKey;
import io.ebean.config.EncryptKeyManager;
import nl.juraji.imagemanager.util.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

/**
 * Created by Juraji on 20-1-2019.
 * Image Manager 2
 */
public class EncryptionKeyManager implements EncryptKeyManager {

    private final Path keyFile;
    private final int keySize;

    public EncryptionKeyManager(String keyFile, int keySize) {
        if ("::UNSAFE".equals(keyFile)) {
            this.keyFile = null;
        } else {
            this.keyFile = Paths.get(keyFile);
        }
        this.keySize = keySize;
    }

    @Override
    public void initialise() {

        try {
            // Check if key file exists
            if (keyFile != null && !FileUtils.exists(keyFile)) {
                // If not, generate random seed and create key_file
                final byte[] bytes = new SecureRandom().generateSeed(keySize);
                final byte[] seed = Base64.getEncoder().encode(bytes);

                Files.write(keyFile, seed);
            }
        } catch (IOException e) {
            throw new EncryptionKeyManagerError(e);
        }
    }

    @Override
    public EncryptKey getEncryptKey(String table, String column) {
        return () -> {
            try {
                final String base = table + "::" + column;

                if (keyFile != null) {
                    final List<String> lines = Files.readAllLines(keyFile);
                    return lines.get(0) + "::" + base;
                } else {
                    return base;
                }

            } catch (IOException e) {
                throw new EncryptionKeyManagerError(e);
            }
        };
    }

    private static class EncryptionKeyManagerError extends RuntimeException {
        public EncryptionKeyManagerError(Throwable cause) {
            super(cause);
        }
    }
}
