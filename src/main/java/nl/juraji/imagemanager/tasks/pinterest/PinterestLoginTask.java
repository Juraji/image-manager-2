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

    public static boolean preTaskCheck(Window stage) {
        // Check if already logged in:
        boolean doLogin = !WebCookieFinder.find().cookieValueEquals(PINTEREST_BASE_URI.getHost(), "_auth", "1");

        if (!doLogin) {
            doLogin = AlertBuilder.confirm(stage)
                    .withTitle("Already authenticated")
                    .withMessage("You have already successfully authenticated with Pinterest, would you like to authenticate with a different account?")
                    .showAndWait();
        }

        if (doLogin) {
            AlertBuilder.info(stage)
                    .withTitle("Alert")
                    .withMessage("You will be presented with a browser to log on to your Pinterest account. " +
                            "Do NOT close the browser, it will close automatically after you have succesfully logged in.")
                    .showAndWait();
        }

        return doLogin;
    }

    @Override
    protected Boolean call() throws Exception {
        // Shutdown WebDriverPool (so it does not retain a session)
        WebDriverPool.shutdown();

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
