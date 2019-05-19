package nl.juraji.imagemanager.util.pivot.menus.builder;

import org.apache.pivot.wtk.Menu;

/**
 * Created by Juraji on 19-5-2019.
 * image-manager
 */
public final class SectionBuilder {
    private MenuBuilder menuBuilder;
    private final Menu.Section section;

    SectionBuilder(MenuBuilder menuBuilder) {
        this.menuBuilder = menuBuilder;
        this.section = new Menu.Section();
        menuBuilder.getMenu().getSections().add(section);
    }

    public SectionBuilder item(Object buttonData, Runnable action, boolean shouldAdd) {
        if (shouldAdd) {
            return item(buttonData, action);
        } else {
            return this;
        }
    }

    public SectionBuilder item(Object buttonData, Runnable action) {
        final Menu.Item item = new Menu.Item(buttonData);
        item.getButtonPressListeners().add(button -> action.run());
        section.add(item);
        return this;
    }

    public SectionBuilder section() {
        return menuBuilder.section();
    }
}
