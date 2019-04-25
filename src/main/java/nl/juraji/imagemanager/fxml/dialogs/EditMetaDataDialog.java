package nl.juraji.imagemanager.fxml.dialogs;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import nl.juraji.imagemanager.model.domain.BaseMetaData;
import nl.juraji.imagemanager.util.DateUtils;
import nl.juraji.imagemanager.util.FileUtils;
import nl.juraji.imagemanager.util.fxml.Controller;
import nl.juraji.imagemanager.util.types.ValueListener;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Juraji on 23-1-2019.
 * Image Manager 2
 */
public class EditMetaDataDialog extends Controller implements Initializable {
    private final SimpleObjectProperty<BaseMetaData> metaData = new SimpleObjectProperty<>();

    public Label dimensionsLabel;
    public Label fileSizeLabel;
    public Label qualityRatingLabel;
    public Label downloadedOnLabel;
    public Label hashCreatedLabel;
    public TextField pathTextField;
    public TextArea commentsTextArea;
    public Button saveButton;

    public void setMetaData(BaseMetaData metaData) {
        this.metaData.set(metaData);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        metaData.addListener((ValueListener<BaseMetaData>) md -> {
            dimensionsLabel.setText(md.getWidth() + "x" + md.getHeight());
            fileSizeLabel.setText(FileUtils.getHumanReadableSize(md.getFileSize()));
            qualityRatingLabel.setText(String.valueOf(md.getQualityRating()));
            downloadedOnLabel.setText(DateUtils.formatDefault(md.getCreated()));
            hashCreatedLabel.setText(md.getHash() != null ? "Yes" : "No");

            pathTextField.setText(md.getPath().toString());
            commentsTextArea.setText(md.getComments());
        });
    }

    @Override
    public void initAccelerators(Map<KeyCombination, Runnable> accelerators) {
        super.initAccelerators(accelerators);

        accelerators.put(KeyCombination.keyCombination("Shortcut+S"), this::save);
    }

    public void save() {
        final BaseMetaData md = this.metaData.get();

        md.setPath(Paths.get(pathTextField.getText()));
        md.setComments(commentsTextArea.getText());

        md.save();
        saveButton.setText("Saved!");
    }

    public void browsePath() {
        final FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(pathTextField.getText()));
        final File file = chooser.showOpenDialog(this.getStage());
        if (file != null) {
            pathTextField.setText(file.getAbsolutePath());
        }
    }
}
