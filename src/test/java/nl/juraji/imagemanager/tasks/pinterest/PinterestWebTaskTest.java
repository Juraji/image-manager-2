package nl.juraji.imagemanager.tasks.pinterest;

import nl.juraji.imagemanager.model.web.pinterest.types.initialstate.InitialStateResource;
import nl.juraji.imagemanager.util.exceptions.ManagerTaskException;
import nl.juraji.imagemanager.util.io.web.WebDriverPool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import util.IMTest;
import util.TestUtils;

import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * Created by Juraji on 10-5-2019.
 * image-manager
 */
public class PinterestWebTaskTest extends IMTest {

    private WebDriverPool webDriverPoolMock;
    private RemoteWebDriver webDriverMock;
    private PinterestWebTask<Void> pinterestWebTask;

    @BeforeEach
    public void setUp() throws Exception {
        this.webDriverPoolMock = Mockito.mock(WebDriverPool.class);
        TestUtils.setStaticFinal(WebDriverPool.class, "INSTANCE", new AtomicReference<>(webDriverPoolMock));

        this.webDriverMock = Mockito.mock(RemoteWebDriver.class);
        Mockito.when(webDriverPoolMock.borrowObject()).thenReturn(webDriverMock);

        this.pinterestWebTask = new PinterestWebTask<Void>("%s %s", "Initial", "message") {
        };
    }

    @Test
    public void initNoDriverAvailable() throws Exception {
        // Simulate fail getting driver
        Mockito.when(webDriverPoolMock.borrowObject()).thenReturn(null);

        assertThrows(ManagerTaskException.class, pinterestWebTask::call, "Failed requesting web driver instance from driver pool");
    }

    @Test
    public void initLoginMissing() {
        // Setup Webdriver mock
        Mockito.when(webDriverMock.getCurrentUrl()).thenReturn("data:,");

        final WebDriver.Options webDriverOptionsMock = Mockito.mock(WebDriver.Options.class);
        Mockito.when(webDriverMock.manage()).thenReturn(webDriverOptionsMock);
        Mockito.when(webDriverOptionsMock.getCookieNamed("_auth")).thenReturn(null);

        assertThrows(ManagerTaskException.class, pinterestWebTask::call);
    }

    @Test
    public void initWithLogin() throws Exception {
        // Setup Webdriver mock
        Mockito.when(webDriverMock.getCurrentUrl()).thenReturn("data:,");

        final WebDriver.Options webDriverOptionsMock = Mockito.mock(WebDriver.Options.class);
        Mockito.when(webDriverMock.manage()).thenReturn(webDriverOptionsMock);
        Mockito.when(webDriverOptionsMock.getCookieNamed("_auth"))
                .thenReturn(new Cookie("_auth", "1", "/", new Date()));

        final RemoteWebElement initialStateElement = Mockito.mock(RemoteWebElement.class);
        Mockito.when(webDriverMock.findElement(By.id("initial-state"))).thenReturn(initialStateElement);
        Mockito.when(initialStateElement.getAttribute("innerHTML")).thenReturn("{\"viewer\":{\"id\":\"1234\",\"username\":\"Juraji\"}}");

        // Should successfully setup driver and log in to Pinterest
        pinterestWebTask.call();

        final InitialStateResource initialState = pinterestWebTask.getInitialState();

        // Should have loaded initial state
        assertEquals("Juraji", initialState.getViewer().getUsername());
        assertEquals("Initial message", pinterestWebTask.getTaskDescription().getValue());
    }

    @Test
    public void initReusedDriverNoLogin() throws Exception {
        // Setup Webdriver mock
        Mockito.when(webDriverMock.getCurrentUrl()).thenReturn("https://pinterest.com");

        final WebDriver.Options webDriverOptionsMock = Mockito.mock(WebDriver.Options.class);
        Mockito.when(webDriverMock.manage()).thenReturn(webDriverOptionsMock);
        Mockito.when(webDriverOptionsMock.getCookieNamed("_auth"))
                .thenReturn(new Cookie("_auth", "0", "/", new Date()));

        assertThrows(ManagerTaskException.class, pinterestWebTask::call, "Not authenticated on Pinterest");
    }

    @Test
    public void initReusedDriverWithLogin() throws Exception {
        // Setup Webdriver mock
        Mockito.when(webDriverMock.getCurrentUrl()).thenReturn("https://pinterest.com");

        final WebDriver.Options webDriverOptionsMock = Mockito.mock(WebDriver.Options.class);
        Mockito.when(webDriverMock.manage()).thenReturn(webDriverOptionsMock);
        Mockito.when(webDriverOptionsMock.getCookieNamed("_auth"))
                .thenReturn(new Cookie("_auth", "1", "/", new Date()));

        final RemoteWebElement initialStateElement = Mockito.mock(RemoteWebElement.class);
        Mockito.when(webDriverMock.findElement(By.id("initial-state"))).thenReturn(initialStateElement);
        Mockito.when(initialStateElement.getAttribute("innerHTML")).thenReturn("{\"viewer\":{\"id\":\"1234\",\"username\":\"Juraji\"}}");

        // Should successfully setup driver and log in to Pinterest
        pinterestWebTask.call();

        // Should have loaded initial state
        final InitialStateResource initialState = pinterestWebTask.getInitialState();
        assertEquals("Juraji", initialState.getViewer().getUsername());
        assertEquals("Initial message", pinterestWebTask.getTaskDescription().getValue());
    }
}
