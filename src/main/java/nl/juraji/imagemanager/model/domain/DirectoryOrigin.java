package nl.juraji.imagemanager.model.domain;

/**
 * Created by Juraji on 14-5-2019.
 * image-manager
 */
public enum DirectoryOrigin {
    DEFAULT("/nl/juraji/imagemanager/images/local.png"),
    LOCAL("/nl/juraji/imagemanager/images/local.png"),
    PINTEREST("/nl/juraji/imagemanager/images/pinterest.png");

    private final String iconResource;

    DirectoryOrigin(String iconResource) {
        this.iconResource = iconResource;
    }

    public String getIconResource() {
        return iconResource;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
