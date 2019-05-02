package nl.juraji.imagemanager.tasks.pinterest;

import com.google.gson.Gson;
import nl.juraji.imagemanager.model.domain.settings.WebCookie;
import nl.juraji.imagemanager.model.finders.WebCookieFinder;
import nl.juraji.imagemanager.model.web.pinterest.resources.ResourceRequest;
import nl.juraji.imagemanager.model.web.pinterest.resources.ResourceResult;
import nl.juraji.imagemanager.model.web.pinterest.types.initialstate.InitialStateObject;
import nl.juraji.imagemanager.util.StringUtils;
import nl.juraji.imagemanager.util.fxml.concurrent.IndicatorTask;
import nl.juraji.imagemanager.util.io.web.WebDriverPool;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * Created by Juraji on 28-11-2018.
 * Image Manager 2
 */
public abstract class PinterestWebTask<T> extends IndicatorTask<T> {

    private static final long WEB_DRIVER_TIMEOUT_SEC = 2;
    private static final long WEB_DRIVER_SLEEP_MILLIS = 500;

    public static final URI PINTEREST_BASE_URI = URI.create("https://pinterest.com");

    private final String originalMessage;
    private final Gson gson;
    private RemoteWebDriver driver;
    private InitialStateObject initialState;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public PinterestWebTask(String message, Object... params) {
        super(message, params);

        try {
            this.originalMessage = this.getMessage();
            this.gson = new Gson();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void done() {
        if (driver != null) {
            // Persist cookies
            this.persistDriverCookies();

            // Return the WebDriver instance to the pool
            WebDriverPool.returnDriver(driver);
        }
    }

    /**
     * @return an InitialStateObject for the current session
     */
    protected InitialStateObject getInitialState() {
        return this.initialState;
    }

    protected String getCSRFToken() {
        final Cookie cookie = this.driver.manage().getCookieNamed("csrftoken");
        return cookie != null ? cookie.getValue() : null;
    }

    protected void init() throws Exception {
        super.updateMessage("Initializing web session...");

        // Get a WebDriver instance from the pool
        this.driver = WebDriverPool.borrowDriver();

        if (driver == null) {
            throw new Exception("Failed requesting web driver instance from driver pool");
        }

        if (this.driver.getCurrentUrl().equals("data:,")) {
            logger.info("Initializing driver for {}", PINTEREST_BASE_URI);
            final String pinterestHomeUri = PINTEREST_BASE_URI.toString();

            // load cookie jar
            this.driver.get(pinterestHomeUri);
            this.loadDriverCookies();
            this.driver.get(pinterestHomeUri);

            if (isUnAuthenticated()) {
                logger.info("Driver: Not authenticated!");
                throw new Exception("Not authenticated on Pinterest");
            }
        }

        // Load initial-state
        final WebElement initialStateElement = getElementBy(By.id("initial-state"));
        this.initialState = gson.fromJson(initialStateElement.getAttribute("innerHTML"), InitialStateObject.class);

        super.updateMessage(originalMessage);
        this.persistDriverCookies();
    }

    protected <U, R extends ResourceResult<U>> R executeResourceRequest(ResourceRequest<R> request) throws Exception {
        logger.info("New Resource request: {}", request.getHeaders());

        String result;
        try (InputStream stream = PinterestWebTask.class.getResourceAsStream("/nl/juraji/imagemanager/tasks/pinterest/PinterestResourceRequestExecutor.js")) {
            String script = IOUtils.toString(stream, StandardCharsets.UTF_8);
            result = (String) driver.executeScript(script,
                    request.getMethod().toString(),
                    request.getResourcePath(),
                    request.getHeaders(),
                    gson.toJson(request));
        }

        if (StringUtils.isNotEmpty(result)) {
            final R parsedResult = gson.fromJson(result, request.getResponseType());

            if (parsedResult.getStatus() != 200) {
                logger.warn("Resource request failed, status: {}", parsedResult.getStatus());
            }

            return parsedResult;
        }

        return null;
    }

    private void loadDriverCookies() {
        final String rootDomain = PINTEREST_BASE_URI.getHost();
        final Set<WebCookie> cookies = WebCookieFinder.find().cookies(rootDomain);
        cookies.stream()
                .map(WebCookie::toCookie)
                .forEach(driver.manage()::addCookie);
    }

    private void persistDriverCookies() {
        final String rootDomain = PINTEREST_BASE_URI.getHost();
        WebCookieFinder.find().saveCookies(rootDomain, driver.manage().getCookies());
    }

    private WebElement getElementBy(By by) {
        return new WebDriverWait(driver, WEB_DRIVER_TIMEOUT_SEC, WEB_DRIVER_SLEEP_MILLIS)
                .until(ExpectedConditions.presenceOfElementLocated(by));
    }

    private boolean isUnAuthenticated() {
        final Cookie authCookie = driver.manage().getCookieNamed("_auth");
        return !(authCookie != null && "1".equals(authCookie.getValue()));
    }
}
