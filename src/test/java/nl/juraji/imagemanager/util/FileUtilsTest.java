package nl.juraji.imagemanager.util;

import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by Juraji on 2-12-2018.
 * Image Manager 2
 */
class FileUtilsTest {
    @Test
    void pathResolving() {
        final URI uri = URI.create("https://pinterest.com/jurajix10/alam-wernick/pin/677932550132956667");
        final URI resolve = uri.resolve("/pin/test");

        assertEquals("https://pinterest.com/pin/test", resolve.toString());
    }
}