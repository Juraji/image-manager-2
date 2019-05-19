package nl.juraji.imagemanager.util.pivot.menus.builder;

import org.apache.pivot.wtk.Menu;

/**
 * Created by Juraji on 15-5-2019.
 * image-manager
 */
public final class MenuBuilder {
    private final Menu menu;

    public MenuBuilder(Menu menu) {
        this.menu = menu;
    }

    public Menu getMenu() {
        return menu;
    }

    public SectionBuilder section() {
        return new SectionBuilder(this);
    }
}
