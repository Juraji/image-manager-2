package nl.juraji.imagemanager.util;


import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;


/**
 * Created by Juraji on 2-12-2018.
 * Image Manager 2
 */
public class FileUtilsTest {
    @Test
    public void pathResolving() {
        final URI uri = URI.create("https://pinterest.com/jurajix10/alam-wernick/pin/677932550132956667");
        final URI resolve = uri.resolve("/pin/test");

        assertEquals("https://pinterest.com/pin/test", resolve.toString());
    }
}
