package nl.juraji.imagemanager.model.domain.pinterest;

/**
 * Created by Juraji on 4-12-2018.
 * Image Manager 2
 */
public enum BoardType {
    BOARD, SECTION;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
