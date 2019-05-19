package nl.juraji.imagemanager.util.io.resources;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.pivot.wtk.media.Picture;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Juraji on 14-5-2019.
 * image-manager
 */
public final class ImageCache {
    private static final ImageCache INSTANCE = new ImageCache();
    private final LoadingCache<String, BufferedImage> cache;

    public ImageCache() {
        cache = CacheBuilder.newBuilder().build(new ImageCache.Loader());
    }

    public static ImageCache getInstance() {
        return INSTANCE;
    }

    public static URL getApplicationIconUrl() {
        return ImageCache.class.getResource("/nl/juraji/imagemanager/images/application.png");
    }

    public BufferedImage get(String url)  {
        return cache.getUnchecked(url);
    }

    public Picture getPicture(String url, int size) {
        final Picture picture = new Picture(get(url));
        picture.resample(size, Picture.Interpolation.BICUBIC);

        return picture;
    }

    private class Loader extends CacheLoader<String, BufferedImage> {
        @Override
        public BufferedImage load(@NotNull String url) throws Exception {
            try (final InputStream stream = ImageCache.class.getResourceAsStream(url)) {
                return ImageIO.read(stream);
            }
        }
    }
}
