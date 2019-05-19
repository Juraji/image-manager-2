package nl.juraji.imagemanager.pivot.sheets;

import nl.juraji.imagemanager.model.domain.settings.Settings;
import nl.juraji.imagemanager.model.finders.SettingsFinder;
import nl.juraji.imagemanager.model.finders.WebCookieFinder;
import nl.juraji.imagemanager.pivot.dialogs.TaskDialog;
import nl.juraji.imagemanager.tasks.pinterest.PinterestLoginTask;
import nl.juraji.imagemanager.tasks.pinterest.PinterestWebTask;
import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.*;

import java.io.File;
import java.net.URL;

/**
 * Created by Juraji on 14-5-2019.
 * image-manager
 */
public class SettingsSheet extends Sheet implements Bindable {

    private final Settings settings;

    @BXML
    private PushButton saveButton;
    @BXML
    private PushButton cancelButton;
    @BXML
    private Label duplicateScannerSimilarityThresholdLabel;
    @BXML
    private Slider duplicateScannerSimilarityThresholdSlider;
    @BXML
    private PushButton pinterestLoginButton;
    @BXML
    private TextInput pinterestTargetDirectoryTextInput;
    @BXML
    private PushButton pinterestTargetDirectoryBrowseButton;
    @BXML
    private ImageView pinterestLoginActiveImage;

    public SettingsSheet() {
        this.settings = SettingsFinder.getSettings();
    }

    @Override
    public void initialize(Map<String, Object> namespace, URL location, Resources resources) {
        this.cancelButton.getButtonPressListeners().add(button -> close());
        this.saveButton.getButtonPressListeners().add(button -> {
            settings.save();
            this.close();
        });

        this.updateDuplicateScannerSimilarityThresholdLabel();

        this.duplicateScannerSimilarityThresholdSlider.setValue(settings.getDuplicateScannerMinSimilarity());
        this.duplicateScannerSimilarityThresholdSlider.getSliderValueListeners().add((slider, previousValue) -> {
            this.settings.setDuplicateScannerMinSimilarity(slider.getValue());
            this.updateDuplicateScannerSimilarityThresholdLabel();
        });

        this.pinterestLoginButton.getButtonPressListeners().add(button -> this.loginOnPinterest());

        this.setPinterestLoginButtonState(false);

        this.pinterestTargetDirectoryTextInput.setText(this.settings.getDefaultTargetDirectory().toString());

        this.pinterestTargetDirectoryBrowseButton.getButtonPressListeners().add(button -> this.browsePinterestTargetDirectory());
    }

    private void loginOnPinterest() {
        TaskDialog.init(this, "Waiting for Pinterest authentication...")
                .thenRun(new PinterestLoginTask(), this::setPinterestLoginButtonState)
                .done();
    }

    private void browsePinterestTargetDirectory() {
        final FileBrowserSheet fileBrowserSheet = new FileBrowserSheet();
        fileBrowserSheet.setMode(FileBrowserSheet.Mode.SAVE_TO);
        fileBrowserSheet.setSelectedFile(settings.getDefaultTargetDirectory().toFile());

        fileBrowserSheet.open(SettingsSheet.this, sheet -> {
            final File newTarget = fileBrowserSheet.getSelectedFile();

            if (newTarget != null) {
                this.settings.setDefaultTargetDirectory(newTarget.toPath());
                this.pinterestTargetDirectoryTextInput.setText(newTarget.toString());
            }
        });
    }

    private void updateDuplicateScannerSimilarityThresholdLabel() {
        this.duplicateScannerSimilarityThresholdLabel.setText(
                String.format("Similarity threshold (%d%%)", settings.getDuplicateScannerMinSimilarity()));
    }

    private void setPinterestLoginButtonState(boolean hasAuth) {
        final boolean authenticated = hasAuth || PinterestLoginTask.hasAuth();
        if (authenticated) {
            this.pinterestLoginButton.setButtonData("You are already logged in!");
        } else {
            this.pinterestLoginButton.setButtonData("Login on Pinterest");
        }
    }
}
