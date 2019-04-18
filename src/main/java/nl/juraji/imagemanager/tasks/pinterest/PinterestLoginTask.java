package nl.juraji.imagemanager.tasks.pinterest;

import javafx.stage.Window;
import nl.juraji.imagemanager.model.finders.WebCookieFinder;
import nl.juraji.imagemanager.util.fxml.AlertBuilder;
import nl.juraji.imagemanager.util.fxml.concurrent.IndicatorTask;
import nl.juraji.imagemanager.util.io.web.ChromeDriverFactory;
import nl.juraji.imagemanager.util.io.web.WebDriverPool;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.RemoteWebDriver;

import static nl.juraji.imagemanager.tasks.pinterest.PinterestWebTask.PINTEREST_BASE_URI;

/**
 * Created by Juraji on 17-4-2019.
 * Image Manager 2
 */
public class PinterestLoginTask extends IndicatorTask<Boolean> {

    public PinterestLoginTask() {
        super("Authenticating on Pinterest...");
    }

    @Override
    protected Boolean call() throws Exception {
        // Clear drivers in the web driver pool, so no session information is kept in memory
        WebDriverPool.clearDrivers();

        // Delete any existing Pinterest cookies
        final String rootDomain = PINTEREST_BASE_URI.getHost();
        WebCookieFinder.find().deleteCookies(rootDomain);

        // Create a new non-headless driver
        final RemoteWebDriver driver = new ChromeDriverFactory(false)
                .create();

        try {
            driver.get(PINTEREST_BASE_URI.toString());

            // Wait for the auth cookie to become present and "1"
            boolean authCookiePresent = false;
            while (!authCookiePresent) {
                final Cookie authCookie = driver.manage().getCookieNamed("_auth");
                authCookiePresent = (authCookie != null && "1".equals(authCookie.getValue()));
                Thread.sleep(500);
            }

            // Persist all cookies
            WebCookieFinder.find().saveCookies(rootDomain, driver.manage().getCookies());

            // Close driver
            driver.close();
        } catch (WebDriverException ignored) {
            // User closed browser
        }

        return WebCookieFinder.find().cookieValueEquals(rootDomain, "_auth", "1");
    }
}
