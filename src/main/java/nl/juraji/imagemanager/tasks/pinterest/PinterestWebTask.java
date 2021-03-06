package nl.juraji.imagemanager.tasks.pinterest;

import com.google.gson.Gson;
import nl.juraji.imagemanager.model.domain.settings.WebCookie;
import nl.juraji.imagemanager.model.finders.WebCookieFinder;
import nl.juraji.imagemanager.model.web.pinterest.resources.ResourceRequest;
import nl.juraji.imagemanager.model.web.pinterest.resources.ResourceResult;
import nl.juraji.imagemanager.model.web.pinterest.types.initialstate.InitialStateResource;
import nl.juraji.imagemanager.util.StringUtils;
import nl.juraji.imagemanager.util.exceptions.ManagerTaskException;
import nl.juraji.imagemanager.util.exceptions.ResourceErrorException;
import nl.juraji.imagemanager.util.exceptions.ResourceNotFoundException;
import nl.juraji.imagemanager.util.fxml.concurrent.ManagerTask;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * Created by Juraji on 28-11-2018.
 * Image Manager 2
 */
public abstract class PinterestWebTask<T> extends ManagerTask<T> {

    private static final long WEB_DRIVER_TIMEOUT_SEC = 2;
    private static final long WEB_DRIVER_SLEEP_MILLIS = 500;

    public static final URI PINTEREST_BASE_URI = URI.create("https://pinterest.com");

    private final Gson gson;
    private RemoteWebDriver driver;
    private InitialStateResource initialState;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public PinterestWebTask(String message, Object... params) {
        super(message, params);
        this.gson = new Gson();
    }

    @Override
    public T call() throws Exception {
        final String orgTaskDescription = this.getTaskDescription();
        super.updateTaskDescription("Initializing web session...");

        // Get a WebDriver instance from the pool
        this.driver = WebDriverPool.borrowDriver();

        if (driver == null) {
            throw new ManagerTaskException("Failed requesting web driver instance from driver pool");
        }

        if (isUnAuthenticated()) {
            // When unauthenticated, authenticate
            logger.info("Initializing driver for {}", PINTEREST_BASE_URI);
            final String pinterestHomeUri = PINTEREST_BASE_URI.toString();

            // load cookie jar
            this.driver.get(pinterestHomeUri);
            this.loadDriverCookies();
            this.driver.get(pinterestHomeUri);
        }

        if (isUnAuthenticated()) {
            // Still unauthenticated
            logger.error("Driver: Not authenticated!");
            throw new ManagerTaskException("Not authenticated on Pinterest");
        }

        // Load initial-state
        final WebElement initialStateElement = getElementBy(By.id("initial-state"));
        this.initialState = gson.fromJson(initialStateElement.getAttribute("innerHTML"), InitialStateResource.class);

        // Refresh persisted cookies
        this.persistDriverCookies();

        super.updateTaskDescription(orgTaskDescription);
        return null;
    }

    @Override
    public void done(boolean success) {
        if (driver != null) {
            if (success) {
                // Persist cookies
                this.persistDriverCookies();
                // Return the WebDriver instance to the pool
                WebDriverPool.returnDriver(driver);
            } else {
                // Something went wrong, invalidate the driver
                WebDriverPool.returnDriverAndInvalidate(driver);
            }

            driver = null;
        }
    }

    /**
     * @return an InitialStateObject for the current session
     */
    protected InitialStateResource getInitialState() {
        return this.initialState;
    }

    protected String getCSRFToken() {
        final Cookie cookie = this.driver.manage().getCookieNamed("csrftoken");
        return cookie != null ? cookie.getValue() : null;
    }

    protected <U, R extends ResourceResult<U>> R executeResourceRequest(ResourceRequest<R> request) throws IOException {
        logger.info("New Resource request: {}", request.getHeaders());

        String result;
        try (InputStream stream = PinterestWebTask.class.getResourceAsStream("/nl/juraji/imagemanager/tasks/pinterest/PinterestResourceRequestExecutor.js")) {
            final String script = IOUtils.toString(stream, StandardCharsets.UTF_8);
            result = (String) driver.executeScript(script,
                    request.getMethod().toString(),
                    request.getResourcePath(),
                    request.getHeaders(),
                    gson.toJson(request));
        }

        if (StringUtils.isNotEmpty(result)) {
            final R parsedResult = gson.fromJson(result, request.getResponseType());

            switch (parsedResult.getStatus()) {
                case 200:
                    return parsedResult;
                case 404:
                    throw new ResourceNotFoundException(request, parsedResult);
                default:
                    throw new ResourceErrorException(request, parsedResult);
            }
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
        WebCookieFinder.find().persistCookies(PINTEREST_BASE_URI.getHost(), driver.manage().getCookies());
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
