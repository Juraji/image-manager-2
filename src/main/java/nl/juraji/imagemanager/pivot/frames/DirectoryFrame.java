package nl.juraji.imagemanager.pivot.frames;

import nl.juraji.imagemanager.model.domain.BaseDirectory;
import nl.juraji.imagemanager.model.domain.BaseMetaData;
import nl.juraji.imagemanager.util.pivot.collections.PivotListCollector;
import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.BeanAdapter;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.List;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.format.DateTimeFormatter;

/**
 * Created by Juraji on 18-5-2019.
 * image-manager
 */
public class DirectoryFrame<D extends BaseDirectory<D, M>, M extends BaseMetaData> extends Frame implements Bindable {

    private BaseDirectory<D, M> directory;

    @BXML
    private TableView tableView;
    @BXML
    private Border imageViewBorder;
    @BXML
    private ImageView imageView;
    @BXML
    private MenuBar imageMenuBar;

    @BXML
    private Label dimensionsLabel;
    @BXML
    private Label indexedOnLabel;

    @Override
    public void initialize(Map<String, Object> namespace, URL location, Resources resources) {
        //noinspection unchecked
        this.directory = (BaseDirectory<D, M>) namespace.get("data");

        this.setIcon(directory.getOrigin().getIconResource());
        this.setTitle(directory.getParentPath());

        final List<BeanAdapter> beanAdapters = directory.getMetaData().stream()
                .map(BeanAdapter::new)
                .collect(new PivotListCollector<>());

        tableView.setTableData(beanAdapters);

        tableView.getTableViewSelectionListeners().add(new TableViewSelectionListener.Adapter() {
            @Override
            public void selectedRowChanged(TableView tableView, Object previousSelectedRow) {
                final ImageView imageView = DirectoryFrame.this.imageView;

                try {
                    final M md = getSelectedMetaData();
                    imageView.setImage(md.getPath().toUri().toURL());
                    dimensionsLabel.setText(md.getWidth() + "x" + md.getHeight());
                    indexedOnLabel.setText(md.getCreated().toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    private M getSelectedMetaData() {
        return (M) ((BeanAdapter) tableView.getSelectedRow()).getBean();
    }
}
