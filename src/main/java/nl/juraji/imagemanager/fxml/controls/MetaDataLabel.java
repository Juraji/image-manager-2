package nl.juraji.imagemanager.fxml.controls;


import javafx.scene.control.Label;
import nl.juraji.imagemanager.model.domain.local.MetaData;

/**
 * Created by Juraji on 2-12-2018.
 * Image Manager 2
 */
public class MetaDataLabel extends Label {
    private final MetaData metaData;

    public MetaDataLabel(MetaData metaData) {
        this.metaData = metaData;

        if (metaData.getPath() != null) {
            this.setText(metaData.getPath().getFileName().toString());
        } else {
            this.setText("**No file known**");
        }
    }

    public MetaData getMetaData() {
        return metaData;
    }
}
