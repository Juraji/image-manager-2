package nl.juraji.imagemanager.fxml.dialogs;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.File;

/**
 * Created by Juraji on 23-11-2018.
 * Image Manager 2
 */
public class AddLocalDirectoryDialog extends Dialog<AddLocalDirectoryDialog.AddDirectoryResult> {

    public AddLocalDirectoryDialog(Window window) {
        // Setup frame
        this.setTitle("Add local directory");
        this.initStyle(StageStyle.UTILITY);
        this.initOwner(window);

        // Setup buttons
        this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Setup content
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(5));

        // Setup content: Path label
        grid.add(new Label("Path"), 0, 0);

        // Setup content: Path text field
        TextField pathField = new TextField();
        pathField.setPromptText("Path");
        Platform.runLater(pathField::requestFocus);
        grid.add(pathField, 1, 0);

        // Setup content: Browse button
        final Button browseButton = new Button("Browse...");
        browseButton.setOnMouseClicked(event -> {
            final DirectoryChooser chooser = new DirectoryChooser();
            chooser.setInitialDirectory(new File(System.getProperty("user.home")));
            final File file = chooser.showDialog(this.getOwner());
            if (file != null) {
                pathField.setText(file.getAbsolutePath());
            }
        });
        grid.add(browseButton, 2, 0);

        // Setup content: Add subdirectories checkbox
        final CheckBox recursiveCheckBox = new CheckBox("Recursive");
        recursiveCheckBox.setSelected(true);
        grid.add(recursiveCheckBox, 1, 1);

        this.getDialogPane().setContent(grid);

        // Bind OK button to pathField value
        final Node okButton = this.getDialogPane().lookupButton(ButtonType.OK);
        pathField.textProperty().addListener((ob, o, n) -> okButton.setDisable(n.trim().isEmpty()));

        // Setup result transformer
        // If OK return chosen File object, else return null
        this.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                return new AddDirectoryResult(pathField.getText(), recursiveCheckBox.isSelected());
            } else {
                return new AddDirectoryResult();
            }
        });
    }

    public static class AddDirectoryResult {
        private final boolean canceled;
        private final File path;
        private final boolean recursive;

        AddDirectoryResult() {
            this.canceled = true;
            this.path = null;
            this.recursive = false;
        }

        AddDirectoryResult(String path, boolean recursive) {
            this.canceled = false;
            this.path = new File(path);
            this.recursive = recursive;
        }

        public boolean isCanceled() {
            return canceled;
        }

        public File getPath() {
            return path;
        }

        public boolean isRecursive() {
            return recursive;
        }
    }
}
