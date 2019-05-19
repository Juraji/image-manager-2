package nl.juraji.imagemanager.util.pivot;

import nl.juraji.imagemanager.util.exceptions.ImageManagerError;
import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.serialization.SerializationException;
import org.apache.pivot.wtk.Window;

import java.io.IOException;

/**
 * Created by Juraji on 13-5-2019.
 * image-manager
 */
public final class BXMLLoader {
    private BXMLLoader() {
        // Private constructor
    }

    public static <T extends Window, U> T load(Class<T> target) {
        return load(target, null);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Window, U> T load(Class<T> target, U data) {
        try {
            final String path = target.getName().replaceAll("\\.", "/");
            final String url = String.format("/%s.bxml", path);
            final BXMLSerializer bxmlSerializer = new BXMLSerializer();
            bxmlSerializer.getNamespace().put("data", data);

            return (T) bxmlSerializer.readObject(target, url);
        } catch (IOException | SerializationException e) {
            throw new ImageManagerError(e);
        }
    }
}
