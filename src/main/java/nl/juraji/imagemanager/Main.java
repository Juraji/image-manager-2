package nl.juraji.imagemanager;

import nl.juraji.imagemanager.pivot.MainWindow;
import nl.juraji.imagemanager.util.pivot.BXMLLoader;
import nl.juraji.imagemanager.util.io.db.EbeanInit;
import org.apache.pivot.collections.Map;
import org.apache.pivot.wtk.Application;
import org.apache.pivot.wtk.DesktopApplicationContext;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.Window;

/**
 * Created by Juraji on 21-11-2018.
 * Image Manager 2
 */
public class Main implements Application {

    private Window window;

    public static void main(String[] args) {
        // Setup database
        EbeanInit.initDataSource();

        // Setup primary stage with root scene and load
        DesktopApplicationContext.main(Main.class, args);
    }

    @Override
    public void startup(Display display, Map<String, String> properties) {
        this.window = BXMLLoader.load(MainWindow.class);
        this.window.open(display);
    }

    @Override
    public boolean shutdown(boolean optional) {
        if (this.window != null) {
            this.window.close();
        }

        EbeanInit.shutdown();

        return false;
    }

    @Override
    public void suspend() {
        // Unused
    }

    @Override
    public void resume() {
        // Unused
    }
}
