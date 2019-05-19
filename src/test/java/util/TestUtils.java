package util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Created by Juraji on 13-5-2019.
 * image-manager
 */
public final class TestUtils {

    public static void setStaticFinal(Class clazz, String field, Object value) throws NoSuchFieldException, IllegalAccessException {
        final Field declaredField = clazz.getDeclaredField(field);
        declaredField.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(declaredField, declaredField.getModifiers() & ~Modifier.FINAL);

        declaredField.set(null, value);
    }
}
