package nl.juraji.imagemanager.fxml.controls;

import javafx.scene.control.Label;
import nl.juraji.imagemanager.model.domain.local.Directory;
import nl.juraji.imagemanager.model.domain.local.MetaData;

import java.util.Set;

/**
 * Created by Juraji on 1-12-2018.
 * Image Manager 2
 */
public class DuplicateSet extends Label {
    private final Set<MetaData> duplicates;

    public DuplicateSet(Directory directory, Set<MetaData> duplicates, int similarity) {
        this.duplicates = duplicates;

        this.setText(String.format("%s: %d items (%d%% sure)", directory.getName(), duplicates.size(), similarity));
    }

    public Set<MetaData> getDuplicates() {
        return duplicates;
    }
}
