package nl.juraji.imagemanager.util;

/**
 * Created by Juraji on 26-11-2018.
 * Image Manager 2
 */
public final class StringUtils {
    private static final int SAFE_FILE_NAME_LENGTH = 64;

    private StringUtils() {
    }

    public static boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }

    public static boolean isNotEmpty(String string) {
        return !isEmpty(string);
    }

    public static String fileNameSafe(String name) {
        if (isEmpty(name)) {
            return name;
        } else {
            String result = name.replaceAll("[^0-9a-zA-Z- ]", "_");
            if (result.length() > SAFE_FILE_NAME_LENGTH) result = result.substring(0, SAFE_FILE_NAME_LENGTH - 1);
            return result.trim();
        }
    }

    public static String capitalize(String text) {
        if (isEmpty(text)) {
            return text;
        } else {
            final String firstChar = text.substring(0, 1);
            final String substring = text.substring(1);

            return firstChar.toUpperCase() + substring.toLowerCase();
        }
    }
}
