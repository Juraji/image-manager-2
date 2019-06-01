package nl.juraji.imagemanager.fxml.controls;

import javafx.geometry.Pos;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import nl.juraji.imagemanager.model.domain.local.Directory;

/**
 * Created by Juraji on 1-12-2018.
 * Image Manager 2
 */
public class TreeItemFavoriteCellFactory implements Callback<TreeTableColumn<Directory, Boolean>, TreeTableCell<Directory, Boolean>> {
    private final Image icon = new Image("/nl/juraji/imagemanager/images/favorite.png", true);

    @Override
    public TreeTableCell<Directory, Boolean> call(TreeTableColumn<Directory, Boolean> param) {
        return new TreeTableCell<Directory, Boolean>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitWidth(16);
                imageView.setFitHeight(16);
                this.setGraphic(imageView);
                this.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || !item) {
                    imageView.setImage(null);
                } else {
                    imageView.setImage(icon);
                }
            }
        };
    }
}
