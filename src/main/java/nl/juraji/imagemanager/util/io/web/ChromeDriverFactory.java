package nl.juraji.imagemanager.util.io.web;

import io.github.bonigarcia.wdm.WebDriverManager;
import nl.juraji.imagemanager.Main;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.logging.Level;

/**
 * Created by Juraji on 28-11-2018.
 * Image Manager 2
 */
public final class ChromeDriverFactory extends BasePooledObjectFactory<RemoteWebDriver> {

    private final ChromeOptions driverOptions;

    public ChromeDriverFactory(boolean headless) {
        final WebDriverManager driverManager = WebDriverManager.chromedriver();
        driverManager.targetPath("./");
        driverManager.setup();

        this.driverOptions = new ChromeOptions();
        this.driverOptions.addArguments("--window-size=1024,768");
        this.driverOptions.setHeadless(!Main.isDebugMode() || headless);

        final LoggingPreferences loggingPreferences = new LoggingPreferences();
        loggingPreferences.enable(LogType.BROWSER, Level.WARNING);
        this.driverOptions.setCapability(CapabilityType.LOGGING_PREFS, loggingPreferences);
    }

    @Override
    public RemoteWebDriver create() {
        return new ChromeDriver(this.driverOptions);
    }

    @Override
    public PooledObject<RemoteWebDriver> wrap(RemoteWebDriver obj) {
        return new DefaultPooledObject<>(obj);
    }

    @Override
    public void destroyObject(PooledObject<RemoteWebDriver> p) {
        final WebDriver driver = p.getObject();
        driver.close();
        driver.quit();
    }

    @Override
    public boolean validateObject(PooledObject<RemoteWebDriver> p) {
        try {
            // Try getting the current url, if that fails the driver instance is broken
            final String currentUrl = p.getObject().getCurrentUrl();
            return !StringUtils.isEmpty(currentUrl);
        } catch (WebDriverException e) {
            return false;
        }
    }
}
