package nl.juraji.imagemanager.tasks.pinterest;

import nl.juraji.imagemanager.model.web.pinterest.types.initialstate.InitialStateResource;
import nl.juraji.imagemanager.util.exceptions.ManagerTaskException;
import nl.juraji.imagemanager.util.io.web.WebDriverPool;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import util.IMTest;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Created by Juraji on 10-5-2019.
 * image-manager
 */
@PrepareForTest({WebDriverPool.class})
public class PinterestWebTaskTest extends IMTest {

    private RemoteWebDriver webDriverMock;
    private PinterestWebTask<Void> pinterestWebTask;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        this.webDriverMock = Mockito.mock(RemoteWebDriver.class);

        PowerMockito.mockStatic(WebDriverPool.class);
        PowerMockito.when(WebDriverPool.borrowDriver()).thenReturn(webDriverMock);

        this.pinterestWebTask = new PinterestWebTask<Void>("%s %s", "Initial", "message") {
        };
    }

    @Test
    public void initNoDriverAvailable() throws Exception {
        // Simulate fail getting driver
        PowerMockito.when(WebDriverPool.borrowDriver()).thenReturn(null);

        // Expect
        expectedException.expect(ManagerTaskException.class);
        expectedException.expectMessage("Failed requesting web driver instance from driver pool");

        // Should fail
        pinterestWebTask.call();
    }

    @Test(expected = ManagerTaskException.class)
    public void initLoginMissing() throws Exception {
        // Setup Webdriver mock
        Mockito.when(webDriverMock.getCurrentUrl()).thenReturn("data:,");

        final WebDriver.Options webDriverOptionsMock = Mockito.mock(WebDriver.Options.class);
        Mockito.when(webDriverMock.manage()).thenReturn(webDriverOptionsMock);
        Mockito.when(webDriverOptionsMock.getCookieNamed("_auth")).thenReturn(null);

        // Should fail
        pinterestWebTask.call();
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
        assertEquals("Initial message", pinterestWebTask.getTaskDescription());
    }

    @Test
    public void initReusedDriverNoLogin() throws Exception {
        // Setup Webdriver mock
        Mockito.when(webDriverMock.getCurrentUrl()).thenReturn("https://pinterest.com");

        final WebDriver.Options webDriverOptionsMock = Mockito.mock(WebDriver.Options.class);
        Mockito.when(webDriverMock.manage()).thenReturn(webDriverOptionsMock);
        Mockito.when(webDriverOptionsMock.getCookieNamed("_auth"))
                .thenReturn(new Cookie("_auth", "0", "/", new Date()));

        // Expect
        expectedException.expect(ManagerTaskException.class);
        expectedException.expectMessage("Not authenticated on Pinterest");

        // Should fail
        pinterestWebTask.call();
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
        assertEquals("Initial message", pinterestWebTask.getTaskDescription());
    }
}
