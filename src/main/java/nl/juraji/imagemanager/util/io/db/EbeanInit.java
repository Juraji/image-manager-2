package nl.juraji.imagemanager.util.io.db;

import io.ebean.EbeanServerFactory;
import io.ebean.config.ServerConfig;
import io.ebean.datasource.DataSourceConfig;

/**
 * Created by Juraji on 29-11-2018.
 * Image Manager 2
 */
public final class EbeanInit {
    private EbeanInit() {
    }

    public static void init() {

        // Setup server configuration
        final ServerConfig serverConfig = new ServerConfig();
        serverConfig.setName("storage");

        // Setup datasource
        final DataSourceConfig storageDb = new DataSourceConfig();
        storageDb.setDriver("org.h2.Driver");
        storageDb.setUsername("sa");
        storageDb.setPassword("sa");
        storageDb.setUrl("jdbc:h2:file:./storage;COMPRESS=TRUE;DEFRAG_ALWAYS=TRUE");

        // Register datasource to server config
        serverConfig.setDataSourceConfig(storageDb);

        // Set config to always run migrations
        serverConfig.setRunMigration(true);

        // Set this server config as server default
        serverConfig.setRegister(true);
        serverConfig.setDefaultServer(true);

        // Set encryption key manager
        serverConfig.setEncryptKeyManager(new EncryptionKeyManager());

        // Start server
        EbeanServerFactory.create(serverConfig);
    }
}
