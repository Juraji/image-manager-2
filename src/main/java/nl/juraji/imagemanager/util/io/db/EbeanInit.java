package nl.juraji.imagemanager.util.io.db;

import io.ebean.EbeanServerFactory;
import io.ebean.config.ServerConfig;

import java.util.Properties;

/**
 * Created by Juraji on 29-11-2018.
 * Image Manager 2
 */
public final class EbeanInit {
    private EbeanInit() {
    }

    public static void initDataSource() {

        // Setup server configuration
        final ServerConfig serverConfig = new ServerConfig();
        serverConfig.setName("db");

        serverConfig.loadFromProperties();

        // Set config to always run migrations
        serverConfig.setRunMigration(true);

        // Set this server config as server default
        serverConfig.setRegister(true);
        serverConfig.setDefaultServer(true);

        // Set encryption key manager
        final Properties configProperties = serverConfig.getProperties();
        final String keyFile = configProperties.getProperty("encryption.keyFile");
        final int keySize = Integer.parseInt(configProperties.getProperty("encryption.size"));
        serverConfig.setEncryptKeyManager(new EncryptionKeyManager(keyFile, keySize));

        // Start server
        EbeanServerFactory.create(serverConfig);
    }
}
