package util;

import io.ebean.annotation.Platform;
import io.ebean.dbmigration.DbMigration;

import java.io.IOException;

/**
 * Created by Juraji on 23-12-2018.
 * Image Manager 2
 */
public class GenerateMigrations {

    /**
     * Will automatically generate migrations based on existing migrations and model,
     * which will be applied during application start
     */
    public static void main(String[] args) throws IOException {
        final DbMigration dbMigration = DbMigration.create();
        dbMigration.setPlatform(Platform.H2);

        System.setProperty("ddl.migration.pendingDropsFor", "1.8");
        dbMigration.generateMigration();
    }
}
