package nl.juraji.imagemanager.fxml.controls;


import javafx.scene.control.Label;
import nl.juraji.imagemanager.model.domain.BaseMetaData;

/**
 * Created by Juraji on 2-12-2018.
 * Image Manager 2
 */
public class MetaDataLabel extends Label {
    private final BaseMetaData metaData;

    public MetaDataLabel(BaseMetaData metaData) {
        this.metaData = metaData;

        if(metaData.getPath()!=null){
            this.setText(metaData.getPath().getFileName().toString());
        } else {
            this.setText("**No file known**");
        }
    }

    public BaseMetaData getMetaData() {
        return metaData;
    }
}
