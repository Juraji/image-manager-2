package nl.juraji.imagemanager.tasks;

import nl.juraji.imagemanager.model.domain.BaseDirectory;
import nl.juraji.imagemanager.model.finders.LocalDirectoriesFinder;
import nl.juraji.imagemanager.model.finders.PinterestBoardsFinder;
import nl.juraji.imagemanager.util.concurrent.ManagerTask;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Juraji on 16-5-2019.
 * image-manager
 */
public class LoadRootDirectoriesTask extends ManagerTask<List<BaseDirectory>> {

    public LoadRootDirectoriesTask() {
        super("Loading directories");
    }

    /**
     * Collect all BaseDirectory type entities without parents
     * Moved to a task, since having a lot of directories may take some time to load,
     * blocking the UI.
     *
     * @return A List of BaseDirectory
     */
    @Override
    public List<BaseDirectory> call() {
        return Stream.concat(
                LocalDirectoriesFinder.find().rootDirectories().stream(),
                PinterestBoardsFinder.find().rootDirectories().stream())
                .sorted(Comparator.comparing(BaseDirectory::getName))
                .collect(Collectors.toList());
    }
}
