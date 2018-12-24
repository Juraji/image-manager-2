package nl.juraji.imagemanager.model.finders;

import io.ebean.Finder;
import nl.juraji.imagemanager.model.domain.BaseDirectory;
import nl.juraji.imagemanager.model.domain.BaseMetaData;
import nl.juraji.imagemanager.model.domain.local.LocalDirectory;
import nl.juraji.imagemanager.model.domain.local.LocalMetaData;
import nl.juraji.imagemanager.model.domain.pinterest.PinMetaData;
import nl.juraji.imagemanager.model.domain.pinterest.PinterestBoard;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Juraji on 1-12-2018.
 * Image Manager 2
 */
public abstract class BaseMetaDataFinder<T extends BaseMetaData> extends Finder<UUID, T> {
    private static final BaseMetaDataFinder<LocalMetaData> LOCAL_META_DATA_INSTANCE = new BaseMetaDataFinder<LocalMetaData>(LocalMetaData.class) {
    };
    private static final BaseMetaDataFinder<PinMetaData> PINTEREST_META_DATA_INSTANCE = new BaseMetaDataFinder<PinMetaData>(PinMetaData.class) {
    };

    private BaseMetaDataFinder(Class<T> type) {
        super(type);
    }

    /**
     * Get all meta data from the database, disregarding directories
     */
    public static List<BaseMetaData> findAllMetaData() {
        return Stream.concat(
                LOCAL_META_DATA_INSTANCE.all().stream(),
                PINTEREST_META_DATA_INSTANCE.all().stream()
        ).collect(Collectors.toList());
    }

    public static BaseDirectory getParent(BaseMetaData metaData) {
        if (metaData instanceof PinMetaData) {
            return ((PinMetaData) metaData).getBoard();
        } else if (metaData instanceof LocalMetaData) {
            return ((LocalMetaData) metaData).getDirectory();
        }

        return null;
    }

    public static void setParent(BaseMetaData metaData, BaseDirectory directory) {
        if (metaData instanceof PinMetaData && directory instanceof PinterestBoard) {
            ((PinMetaData) metaData).setBoard((PinterestBoard) directory);
        } else if (metaData instanceof LocalMetaData && directory instanceof LocalDirectory) {
            ((LocalMetaData) metaData).setDirectory((LocalDirectory) directory);
        } else {
            throw new UnsupportedDirectoryTypeException(metaData, directory);
        }
    }

    /**
     * Created by Juraji on 14-12-2018.
     * Image Manager 2
     */
    public static class UnsupportedDirectoryTypeException extends UnsupportedOperationException {
        public UnsupportedDirectoryTypeException(BaseMetaData target, BaseDirectory directory) {
            super(String.format("Can not set directory of type %s as parent of %s", directory.getClass(), target.getClass()));
        }
    }
}
