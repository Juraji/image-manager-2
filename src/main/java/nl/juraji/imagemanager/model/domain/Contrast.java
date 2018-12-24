package nl.juraji.imagemanager.model.domain;

/**
 * Created by Juraji on 21-11-2018.
 * Image Manager 2
 */
public enum Contrast {
    LIGHT, DARK;

    public Contrast invert() {
        return LIGHT.equals(this) ? DARK : LIGHT;
    }
}
