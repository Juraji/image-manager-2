package nl.juraji.imagemanager;

/**
 * Created by Juraji on 18-4-2019.
 * Image Manager 2
 */
public final class Bootstrap {

    /**
     * Used as entry class, since OpenJDK 11 does not directly support JavaFx.
     * OpenJFX is packaged within the shaded JAR, it will take over when Main is bootstrapped
     *
     * @param args Application arguments
     */
    public static void main(String[] args) {
        Main.init(args);
    }
}
