package nl.juraji.imagemanager.fxml;

import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import nl.juraji.imagemanager.model.domain.PinterestSettings;
import nl.juraji.imagemanager.model.domain.Settings;
import nl.juraji.imagemanager.model.finders.SettingsFinder;
import nl.juraji.imagemanager.util.Crypt;
import nl.juraji.imagemanager.util.StringUtils;
import nl.juraji.imagemanager.util.fxml.Controller;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ResourceBundle;

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
    public TextField pinterestUsernameTextField;
    public PasswordField pinterestPasswordField;

    public SettingsWindow() {
        this.settings = SettingsFinder.getSettings();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Application Settings
        duplicateScannerMinSimilaritySlider.setValue(settings.getDuplicateScannerMinSimilarity());
        defaultTargetDirectoryTextField.setText(settings.getDefaultTargetDirectory().toString());

        // Pinterest Settings
        final PinterestSettings pinterestSettings = settings.getPinterestSettings();
        pinterestUsernameTextField.setText(pinterestSettings.getPinterestUsername());

        if (pinterestSettings.getPinterestPassword() != null) {
            pinterestPasswordField.setText(DUMMY_PASSWORD);
        }

        duplicateScannerMinSimilarityLabel.textProperty().bind(duplicateScannerMinSimilaritySlider.valueProperty().asString("%.0f%%"));
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
        try {
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

            // Pinterest settings
            final PinterestSettings pinterestSettings = settings.getPinterestSettings();
            final String pinterestUsername = pinterestUsernameTextField.getText();
            if (StringUtils.isNotEmpty(pinterestUsername)) {
                pinterestSettings.setPinterestUsername(pinterestUsername);
                doPersist = true;
            }

            final String pinterestPassword = pinterestPasswordField.getText();
            if (StringUtils.isNotEmpty(pinterestPassword) && !DUMMY_PASSWORD.equals(pinterestPassword)) {
                final byte[] encryptedPassword = Crypt.init(pinterestSettings.getPasswordSalt()).encrypt(pinterestPassword);
                pinterestSettings.setPinterestPassword(encryptedPassword);
                doPersist = true;
            }

            if (doPersist) {
                settings.save();
            }

            this.close();
        } catch (GeneralSecurityException e) {
            LoggerFactory.getLogger(this.getClass()).error("Failed persisting settings", e);
        }
    }
}
