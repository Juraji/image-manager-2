package nl.juraji.imagemanager.pivot.sheets;

import nl.juraji.imagemanager.model.domain.pinterest.PinterestBoard;
import nl.juraji.imagemanager.pivot.dialogs.TaskDialog;
import nl.juraji.imagemanager.tasks.pinterest.FetchPinterestBoardsTask;
import nl.juraji.imagemanager.tasks.pinterest.PinterestLoginTask;
import nl.juraji.imagemanager.util.pivot.Alerts;
import nl.juraji.imagemanager.util.pivot.EDT;
import nl.juraji.imagemanager.util.pivot.collections.PivotListCollector;
import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.BeanAdapter;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.Sheet;
import org.apache.pivot.wtk.TableView;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Juraji on 16-5-2019.
 * image-manager
 */
public class PinterestBoardsSheet extends Sheet implements Bindable {

    @BXML
    private TableView tableView;
    @BXML
    private PushButton addButton;
    @BXML
    private PushButton cancelButton;

    @Override
    public void initialize(Map<String, Object> namespace, URL location, Resources resources) {
        // Run after display is initialized
        EDT.run(() -> {
            if (!PinterestLoginTask.hasAuth()) {
                Alerts.warn(this, "You are not logged in to Pinterest, " +
                        "open Settings via the File menu and click \"Login on Pinterest\".");
                this.close();
            }

            TaskDialog.init(this, "Fetching Pinterest boards...")
                    .thenRun(new FetchPinterestBoardsTask(), this::loadBoards)
                    .done();
        });

        this.addButton.getButtonPressListeners().add(button -> this.close(true));
        this.cancelButton.getButtonPressListeners().add(button -> this.close(false));
    }

    public List<PinterestBoard> getSelectedBoards() {
        return PivotListCollector.pivotListToJavaList((ArrayList<?>) this.tableView.getSelectedRows())
                .stream()
                .map(o -> (PinterestBoard) ((BeanAdapter) o).getBean())
                .collect(Collectors.toList());
    }

    private void loadBoards(List<PinterestBoard> boards) {
        final org.apache.pivot.collections.List<BeanAdapter> collect = boards.stream()
                .map(BeanAdapter::new)
                .collect(new PivotListCollector<>());

        this.tableView.setTableData(collect);
    }
}
