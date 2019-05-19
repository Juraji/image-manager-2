package nl.juraji.imagemanager.util.pivot.menus;

import nl.juraji.imagemanager.util.pivot.menus.builder.MenuBuilder;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.Menu;
import org.apache.pivot.wtk.MenuHandler;

/**
 * Created by Juraji on 16-5-2019.
 * image-manager
 */
public abstract class ContextMenuHandler extends MenuHandler.Adapter {
    @Override
    public final boolean configureContextMenu(Component component, Menu menu, int x, int y) {
        this.buildMenu(new MenuBuilder(menu), x, y);
        return false;
    }

    protected abstract void buildMenu(MenuBuilder builder, int x, int y);
}
