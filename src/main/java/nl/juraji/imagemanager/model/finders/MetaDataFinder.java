package nl.juraji.imagemanager.model.finders;

import io.ebean.Finder;
import nl.juraji.imagemanager.model.domain.local.MetaData;

import java.util.UUID;

public class MetaDataFinder extends Finder<UUID, MetaData> {
    private static final MetaDataFinder INSTANCE = new MetaDataFinder();

    private MetaDataFinder() {
        super(MetaData.class);
    }

    public static MetaDataFinder find() {
        return INSTANCE;
    }
}
