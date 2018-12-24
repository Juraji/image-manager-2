package nl.juraji.imagemanager.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Juraji on 24-11-2018.
 * Image Manager 2
 */
public final class CollectionUtils {
    private CollectionUtils() {
    }

    public static <T> List<T> init(List<T> list) {
        if (list == null) {
            return new ArrayList<>();
        }
        return list;
    }

    public static <T> Set<T> init(Set<T> set) {
        if (set == null) {
            return new HashSet<>();
        }

        return set;
    }
}
