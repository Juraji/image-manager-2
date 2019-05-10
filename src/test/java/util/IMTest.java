package util;

import com.sun.javafx.application.PlatformImpl;
import javafx.application.Platform;
import nl.juraji.imagemanager.util.io.db.EbeanInit;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Juraji on 10-5-2019.
 * image-manager
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Platform.class, PlatformImpl.class})
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.dom.*"})
public abstract class IMTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(IMTest.class);

    @BeforeClass
    public static void setUpEbean() {
        // Load Ebean mode
        LOGGER.info("Initialize Ebean...");
        EbeanInit.initDataSource();
        LOGGER.info("Initialization completed!");
    }

    @Before
    public void setUpFXPlatformMock() throws Exception {
        LOGGER.info("Mocking Platform#runLater ");
        PowerMockito.mockStatic(PlatformImpl.class);
        PowerMockito.doAnswer(invocationOnMock -> {
            Runnable runnable = invocationOnMock.getArgument(0);
            runnable.run();
            return null;
        }).when(PlatformImpl.class, "runLater", Mockito.any());
    }
}
