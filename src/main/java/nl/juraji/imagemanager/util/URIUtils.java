package nl.juraji.imagemanager.util;

import java.net.URI;

/**
 * Created by Juraji on 9-12-2018.
 * Image Manager 2
 */
public final class URIUtils {
    private URIUtils() {
    }

    public static String getPathSection(URI uri, int index) {
        final String path = trimSlashes(uri.getPath());
        final String[] segments = path.split("/");

        if (index < 0) {
            index = segments.length + index;
        }

        return segments[index];
    }

    public static String trimSlashes(String string) {
        return string
                .replaceAll("/+$", "")
                .replaceAll("^/+", "");
    }

    public static boolean contains(URI uri, String s) {
        return !(uri == null || StringUtils.isEmpty(s)) && uri.toString().contains(s);
    }
}
