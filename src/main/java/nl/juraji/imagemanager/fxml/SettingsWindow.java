package nl.juraji.imagemanager.fxml;

import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import nl.juraji.imagemanager.fxml.dialogs.WorkDialog;
import nl.juraji.imagemanager.model.domain.settings.Settings;
import nl.juraji.imagemanager.model.finders.SettingsFinder;
import nl.juraji.imagemanager.model.finders.WebCookieFinder;
import nl.juraji.imagemanager.tasks.pinterest.PinterestLoginTask;
import nl.juraji.imagemanager.util.StringUtils;
import nl.juraji.imagemanager.util.fxml.AlertBuilder;
import nl.juraji.imagemanager.util.fxml.Controller;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import static nl.juraji.imagemanager.tasks.pinterest.PinterestWebTask.PINTEREST_BASE_URI;

/**
 * Created by Juraji on 27-11-2018.
 * Image Manager 2
 */
public class SettingsWindow extends Controller implements Initializable {
    private static final String DUMMY_PASSWORD = "DUMMY_PASSWORD";

    private final Settings settings;
    public Slider duplicateScannerMinSimilaritySlider;
    public Label duplicateScannerMinSimilarityLabel;
    public TextField defaultTargetDirectoryTextField;

    public SettingsWindow() {
        this.settings = SettingsFinder.getSettings();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Application Settings
        duplicateScannerMinSimilaritySlider.setValue(settings.getDuplicateScannerMinSimilarity());
        defaultTargetDirectoryTextField.setText(settings.getDefaultTargetDirectory().toString());


        duplicateScannerMinSimilarityLabel.textProperty().bind(duplicateScannerMinSimilaritySlider.valueProperty().asString("%.0f%%"));
    }

    public void runPinterestLogin() {
        // Check if already logged in:
        boolean doLogin = !WebCookieFinder.find().cookieValueEquals(PINTEREST_BASE_URI.getHost(), "_auth", "1");

        if (!doLogin) {
            doLogin = AlertBuilder.confirm(getStage())
                    .withTitle("Already authenticated")
                    .withMessage("You have already successfully authenticated with Pinterest, would you like to authenticate with a different account?")
                    .showAndWait();
        }

        if (doLogin) {
            AlertBuilder.info(getStage())
                    .withTitle("Alert")
                    .withMessage("You will be presented with a browser to log on to your Pinterest account. " +
                            "Do NOT close the browser, it will close automatically after you have successfully logged in.")
                    .showAndWait();
        }

        if (doLogin) {
            final WorkDialog<Boolean> dialog = new WorkDialog<>(getStage());

            dialog.addTaskEndNotification(success -> {
                if (success) {
                    AlertBuilder.info(getStage())
                            .withTitle("Login success")
                            .withMessage("Pinterest authentication successful!")
                            .show();
                } else {
                    AlertBuilder.warning(getStage())
                            .withTitle("Login failed")
                            .withMessage("Pinterest authentication failed! Be sure to not close the browser window, it will close by itself.")
                            .show();
                }
            });

            dialog.exec(new PinterestLoginTask(), true);
        }
    }

    public void browseDefaultTargetDirectory() {
        final DirectoryChooser chooser = new DirectoryChooser();
        final File current = new File(defaultTargetDirectoryTextField.getText());
        if (current.exists()) {
            chooser.setInitialDirectory(current);
        } else {
            chooser.setInitialDirectory(settings.getDefaultTargetDirectory().toFile());
        }
        final File file = chooser.showDialog(getStage());
        if (file != null) {
            defaultTargetDirectoryTextField.setText(file.getAbsolutePath());
        }
    }

    public void saveSettingsAction() {
        boolean doPersist = false;

        // Duplicate Scanner settings
        final double minimalSimilarity = duplicateScannerMinSimilaritySlider.getValue();
        if (minimalSimilarity != settings.getDuplicateScannerMinSimilarity()) {
            settings.setDuplicateScannerMinSimilarity((int) minimalSimilarity);
            doPersist = true;
        }

        final String defaultTargetDirectory = defaultTargetDirectoryTextField.getText();
        if (StringUtils.isNotEmpty(defaultTargetDirectory)) {
            settings.setDefaultTargetDirectory(Paths.get(defaultTargetDirectory));
            doPersist = true;
        }

        if (doPersist) {
            settings.save();
        }

        this.close();
    }
}
