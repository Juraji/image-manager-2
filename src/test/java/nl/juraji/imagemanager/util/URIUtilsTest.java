package nl.juraji.imagemanager.util;

import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * Created by Juraji on 9-12-2018.
 * Image Manager 2
 */
public class URIUtilsTest {

    @Test
    public void getPathSection() {
        final URI uri = URI.create("https://juraji.nl/some/awesome/path/");

        assertEquals("some", URIUtils.getPathSection(uri, 0));
        assertEquals("awesome", URIUtils.getPathSection(uri, 1));
        assertEquals("path", URIUtils.getPathSection(uri, -1));
        assertEquals("awesome", URIUtils.getPathSection(uri, -2));
    }

    @Test
    public void getPathSectionOOB() {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            final URI uri = URI.create("https://juraji.nl/some/awesome/path/");
            URIUtils.getPathSection(uri, -6);
        });
    }

    @Test
    public void trimSlashes() {
        String t1 = "/some/path";
        String t2 = "some/path/";
        String t3 = "/some/path/";
        String t4 = "//some///broken//path/////";

        assertEquals("some/path", URIUtils.trimSlashes(t1));
        assertEquals("some/path", URIUtils.trimSlashes(t2));
        assertEquals("some/path", URIUtils.trimSlashes(t3));
        assertEquals("some///broken//path", URIUtils.trimSlashes(t4));
    }
}
