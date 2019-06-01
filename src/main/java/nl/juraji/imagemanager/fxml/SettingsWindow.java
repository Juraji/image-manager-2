package nl.juraji.imagemanager.fxml;

import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import nl.juraji.imagemanager.model.domain.settings.Settings;
import nl.juraji.imagemanager.model.finders.SettingsFinder;
import nl.juraji.imagemanager.util.fxml.Controller;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Juraji on 27-11-2018.
 * Image Manager 2
 */
public class SettingsWindow extends Controller implements Initializable {
    private final Settings settings;
    public Slider duplicateScannerMinSimilaritySlider;
    public Label duplicateScannerMinSimilarityLabel;

    public SettingsWindow() {
        this.settings = SettingsFinder.getSettings();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Application Settings
        duplicateScannerMinSimilaritySlider.setValue(settings.getDuplicateScannerMinSimilarity());
        duplicateScannerMinSimilarityLabel.textProperty().bind(duplicateScannerMinSimilaritySlider.valueProperty().asString("%.0f%%"));
    }

    public void saveSettingsAction() {
        boolean doPersist = false;

        // Duplicate Scanner settings
        final double minimalSimilarity = duplicateScannerMinSimilaritySlider.getValue();
        if (minimalSimilarity != settings.getDuplicateScannerMinSimilarity()) {
            settings.setDuplicateScannerMinSimilarity((int) minimalSimilarity);
            doPersist = true;
        }

        if (doPersist) {
            settings.save();
        }

        this.close();
    }
}
