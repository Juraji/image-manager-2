package nl.juraji.imagemanager.util.io.web;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Juraji on 28-11-2018.
 * Image Manager 2
 */
public final class WebDriverPool extends GenericObjectPool<RemoteWebDriver> {
    private static final AtomicReference<WebDriverPool> INSTANCE = new AtomicReference<>();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public WebDriverPool() {
        super(new ChromeDriverFactory(true));
        this.setBlockWhenExhausted(true);
        this.setMaxTotal(1);
        this.setTestOnBorrow(true);
        this.setTestOnReturn(true);
        this.setMinEvictableIdleTimeMillis(TimeUnit.MINUTES.toMillis(1));
    }

    public static RemoteWebDriver borrowDriver() throws Exception {
        synchronized (WebDriverPool.class) {
            if (INSTANCE.get() == null) {
                final WebDriverPool pool = new WebDriverPool();
                pool.preparePool();
                INSTANCE.set(pool);

                // Self shutdown on system exit
                Runtime.getRuntime().addShutdownHook(new Thread(pool::close));
            }
        }

        return INSTANCE.get().borrowObject();
    }

    public static void returnDriver(RemoteWebDriver driver) {
        INSTANCE.get().returnObject(driver);
    }

    public static void returnDriverAndInvalidate(RemoteWebDriver driver) {
        final WebDriverPool pool = INSTANCE.get();
        if (pool != null) {
            try {
                pool.invalidateObject(driver);
            } catch (Exception e) {
                // Ignored
            }
        }
    }

    public static void clearDrivers() {
        final WebDriverPool pool = INSTANCE.get();
        if (pool != null) {
            pool.clear();
        }
    }

    @Override
    public RemoteWebDriver borrowObject() throws Exception {
        logger.info("Driver instance requested");
        final RemoteWebDriver driver = super.borrowObject();
        logger.info("Driver instance available: " + driver.getSessionId());
        return driver;
    }

    @Override
    public void returnObject(RemoteWebDriver driver) {
        if (driver != null) {
            logger.info("Driver instance returned " + driver.getSessionId());
            super.returnObject(driver);
        }
    }

    @Override
    public void close() {
        logger.info("Close requested, quitting existing driver instances...");
        super.close();
    }
}
