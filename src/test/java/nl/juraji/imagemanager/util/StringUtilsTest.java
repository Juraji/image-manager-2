package nl.juraji.imagemanager.util;


import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Juraji on 27-11-2018.
 * Image Manager 2
 */
@SuppressWarnings("ConstantConditions")
public class StringUtilsTest {

    @Test
    public void isEmpty() {
        assertTrue(StringUtils.isEmpty(""));
        assertTrue(StringUtils.isEmpty(null));
        assertFalse(StringUtils.isEmpty("some text"));
    }

    @Test
    public void isNotEmpty() {
        assertFalse(StringUtils.isNotEmpty(""));
        assertFalse(StringUtils.isNotEmpty(null));
        assertTrue(StringUtils.isNotEmpty("some text"));
    }
}
