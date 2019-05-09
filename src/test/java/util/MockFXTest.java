package util;

import com.sun.javafx.application.PlatformImpl;
import javafx.application.Platform;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by Juraji on 10-5-2019.
 * image-manager
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Platform.class, PlatformImpl.class})
@PowerMockIgnore({"javax.xml.parsers.*"})
public abstract class MockFXTest {

    @Before
    public void setUpFXPlatformMock() throws Exception {
        PowerMockito.mockStatic(PlatformImpl.class);
        PowerMockito.doAnswer(invocationOnMock -> {
            Runnable runnable = invocationOnMock.getArgument(0);
            runnable.run();
            return null;
        }).when(PlatformImpl.class, "runLater", Mockito.any());
    }
}
