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
    private static final Path KEY_FILE = Paths.get("./key_file");
    private static final int KEY_SIZE = 265;

    @Override
    public void initialise() {

        try {
            // Check if key file exists
            if (!FileUtils.exists(KEY_FILE)) {
                // If not, generate random seed and create key_file
                final byte[] bytes = new SecureRandom().generateSeed(KEY_SIZE);
                final byte[] seed = Base64.getEncoder().encode(bytes);


                Files.write(KEY_FILE, seed);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EncryptKey getEncryptKey(String table, String column) {
        return () -> {
            try {
                final List<String> lines = Files.readAllLines(KEY_FILE);
                return lines.get(0) + "::" + table + "::" + column;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
