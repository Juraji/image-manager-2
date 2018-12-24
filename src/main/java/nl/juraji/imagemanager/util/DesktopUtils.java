package nl.juraji.imagemanager.util;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

/**
 * Created by Juraji on 1-12-2018.
 * Image Manager 2
 */
public final class DesktopUtils {
    private DesktopUtils() {
    }

    public static void openFile(Path path) {
        try {
            Desktop.getDesktop().open(path.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void openWebUri(URI uri) {
        try {
            Desktop.getDesktop().browse(uri);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
