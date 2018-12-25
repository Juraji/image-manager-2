package nl.juraji.imagemanager.fxml;

import javafx.animation.PauseTransition;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.layout.TilePane;
import javafx.util.Duration;
import nl.juraji.imagemanager.fxml.controls.DuplicateSet;
import nl.juraji.imagemanager.fxml.controls.MetaDataTile;
import nl.juraji.imagemanager.fxml.dialogs.WorkDialog;
import nl.juraji.imagemanager.fxml.dialogs.WorkQueueDialog;
import nl.juraji.imagemanager.model.domain.BaseDirectory;
import nl.juraji.imagemanager.model.domain.BaseMetaData;
import nl.juraji.imagemanager.model.domain.DefaultDirectory;
import nl.juraji.imagemanager.model.domain.settings.Settings;
import nl.juraji.imagemanager.model.finders.BaseDirectoryFinder;
import nl.juraji.imagemanager.model.finders.BaseMetaDataFinder;
import nl.juraji.imagemanager.model.finders.SettingsFinder;
import nl.juraji.imagemanager.tasks.DuplicateScanTask;
import nl.juraji.imagemanager.util.fxml.Controller;
import nl.juraji.imagemanager.util.fxml.OptionDialogBuilder;
import nl.juraji.imagemanager.util.types.ValueListener;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Juraji on 26-11-2018.
 * Image Manager 2
 */
public class DuplicateScannerWindow extends Controller implements Initializable {

    private final Settings settings;

    public ListView<DuplicateSet> duplicateSetList;
    public TilePane duplicateSetView;
    public Slider minSimilaritySlider;

    public DuplicateScannerWindow() {
        this.settings = SettingsFinder.getSettings();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        minSimilaritySlider.setValue(settings.getDuplicateScannerMinSimilarity());

        final PauseTransition pauseTransition = new PauseTransition(Duration.millis(200));
        minSimilaritySlider.valueProperty().addListener((ValueListener<Number>) newValue -> {
            pauseTransition.setOnFinished(e -> {
                settings.setDuplicateScannerMinSimilarity(newValue.intValue());
                settings.save();
            });
            pauseTransition.playFromStart();
        });

        this.duplicateSetList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.duplicateSetList.getSelectionModel().selectedItemProperty()
                .addListener((ValueListener<DuplicateSet>) this::renderDuplicateSetViewItems);
    }

    public void scan(List<BaseDirectory> directories) {
        this.runScanPerDirectory(directories);
    }

    public void startScanAction() {
        final int optionIndex = OptionDialogBuilder.build(getStage())
                .withTitle("Start a new scan")
                .withMessage("How would you like the scanner to compare indices?")
                .withOption("Scan per directory")
                .withOption("Scan across all directories")
                .show();

        switch (optionIndex) {
            case 0:
                this.runScanPerDirectory(null);
                break;
            case 1:
                this.runScanAcrossDirectories();
                break;
        }
    }

    private void runScanPerDirectory(List<BaseDirectory> directories) {
        this.duplicateSetList.getItems().clear();
        this.duplicateSetView.getChildren().clear();
        this.duplicateSetList.getSelectionModel().clearSelection();

        final WorkQueueDialog<List<DuplicateSet>> wd = new WorkQueueDialog<>(getStage());

        if (directories == null) {
            directories = BaseDirectoryFinder.findAllRootDirectories();
        }

        directories.forEach(directory -> wd.queue(new DuplicateScanTask(directory, minSimilaritySlider.getValue())));
        wd.addTaskEndNotification(list -> this.duplicateSetList.getItems().addAll(list));
        wd.addQueueEndNotification(() -> {
            if (this.duplicateSetList.getItems().size() > 0) {
                this.duplicateSetList.getSelectionModel().select(0);
                this.duplicateSetList.requestFocus();
            }
        });

        wd.execute();
    }

    private void runScanAcrossDirectories() {
        this.duplicateSetList.getItems().clear();
        this.duplicateSetView.getChildren().clear();
        this.duplicateSetList.getSelectionModel().clearSelection();

        final List<BaseMetaData> allMetaData = BaseMetaDataFinder.findAllMetaData();
        final DefaultDirectory directory = new DefaultDirectory();
        directory.setName("All directories");
        directory.getMetaData().addAll(allMetaData);

        final WorkDialog<List<DuplicateSet>> wd = new WorkDialog<>(getStage());

        wd.addTaskEndNotification(list -> {
            if (!list.isEmpty()) {
                this.duplicateSetList.getItems().addAll(list);
                this.duplicateSetList.getSelectionModel().select(0);
                this.duplicateSetList.requestFocus();
            }
        });

        wd.exec(new DuplicateScanTask(directory, minSimilaritySlider.getValue()));
    }

    private void renderDuplicateSetViewItems(DuplicateSet set) {
        final ObservableList<Node> children = this.duplicateSetView.getChildren();

        children.clear();

        if (set != null) {
            set.getDuplicates().stream()
                    .sorted(Comparator.comparingLong(BaseMetaData::getQualityRating).reversed())
                    .map(MetaDataTile::new)
                    .forEach(children::add);
        }
    }
}
