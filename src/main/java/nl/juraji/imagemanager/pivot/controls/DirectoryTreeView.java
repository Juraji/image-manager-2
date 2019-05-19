package nl.juraji.imagemanager.pivot.controls;

import nl.juraji.imagemanager.model.domain.BaseDirectory;
import nl.juraji.imagemanager.model.domain.DirectoryOrigin;
import nl.juraji.imagemanager.util.io.resources.ImageCache;
import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.List;
import org.apache.pivot.collections.Sequence;
import org.apache.pivot.wtk.TreeView;
import org.apache.pivot.wtk.content.TreeBranch;
import org.apache.pivot.wtk.content.TreeNode;
import org.apache.pivot.wtk.media.Image;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Juraji on 15-5-2019.
 * image-manager
 */
public class DirectoryTreeView extends TreeView {
    public void setDirectories(Collection<BaseDirectory> directories) {
        List<TreeNode> treeNodes = mapDirectories(directories);
        this.setTreeData(treeNodes);
        this.expandAll();
    }

    public DirectoryTreeView() {
    }

    private List<TreeNode> mapDirectories(Collection<BaseDirectory> directories) {
        final ArrayList<TreeNode> treeNodes = new ArrayList<>();

        for (BaseDirectory directory : directories) {
            if (directory.getChildren().isEmpty()) {
                treeNodes.add(new DirectoryTreeNode(directory));
            } else {
                final DirectoryTreeBranch treeBranch = new DirectoryTreeBranch(directory);
                treeNodes.add(treeBranch);
                //noinspection unchecked
                mapDirectories((Collection<BaseDirectory>) directory.getChildren()).forEach(treeBranch::add);
            }
        }

        return treeNodes;
    }

    private static Image getDirectoryIcon(DirectoryOrigin origin) {
        return ImageCache.getInstance().getPicture(origin.getIconResource(), 16);
    }

    private static String buildLabel(BaseDirectory directory) {
        return String.format("%s (%d images)", directory.getName(), directory.getMetaDataCount());
    }

    public DirectoryNode selectNodeAt(int y) {
        this.setSelectedPath(this.getNodeAt(y));
        return (DirectoryNode) this.getSelectedNode();
    }

    public void removeNodeAt(Sequence.Tree.Path path) {
        if (path.getLength() == 1) {
            // Remove top-level node
            this.getTreeData().remove(path.get(0), 1);
        } else {
            // Remove node down the hierarchy
            final Sequence.Tree.Path copy = new Sequence.Tree.Path(path);
            final int remove = copy.remove(copy.getLength() - 1, 1).get(0);
            final Iterator<Integer> iterator = copy.iterator();
            List current = this.getTreeData();

            while (iterator.hasNext()) {
                current = (List) current.get(iterator.next());
            }

            current.remove(remove, 1);
        }
    }

    public interface DirectoryNode {
        BaseDirectory getDirectory();
    }

    private class DirectoryTreeNode extends TreeNode implements DirectoryNode {
        private final BaseDirectory directory;

        private DirectoryTreeNode(BaseDirectory directory) {
            super(getDirectoryIcon(directory.getOrigin()), buildLabel(directory));
            this.directory = directory;
        }

        public BaseDirectory getDirectory() {
            return directory;
        }
    }

    private class DirectoryTreeBranch extends TreeBranch implements DirectoryNode {
        private final BaseDirectory directory;

        private DirectoryTreeBranch(BaseDirectory directory) {
            super(getDirectoryIcon(directory.getOrigin()), buildLabel(directory));
            this.directory = directory;
        }

        public BaseDirectory getDirectory() {
            return directory;
        }
    }
}
