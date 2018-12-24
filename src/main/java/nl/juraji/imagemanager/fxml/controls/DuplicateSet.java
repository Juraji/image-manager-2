package nl.juraji.imagemanager.fxml.controls;

import javafx.scene.control.Label;
import nl.juraji.imagemanager.model.domain.BaseDirectory;
import nl.juraji.imagemanager.model.domain.BaseMetaData;

import java.util.List;

/**
 * Created by Juraji on 1-12-2018.
 * Image Manager 2
 */
public class DuplicateSet extends Label {
    private final List<BaseMetaData> duplicates;

    public DuplicateSet(BaseDirectory directory, List<BaseMetaData> duplicates, int similarity) {
        this.duplicates = duplicates;

        this.setText(String.format("%s: %d items (%d%% sure)", directory.getName(), duplicates.size(), similarity));
    }

    public List<BaseMetaData> getDuplicates() {
        return duplicates;
    }
}
