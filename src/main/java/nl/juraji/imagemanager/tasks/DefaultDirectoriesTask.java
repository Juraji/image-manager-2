package nl.juraji.imagemanager.tasks;

import nl.juraji.imagemanager.model.domain.local.Directory;
import nl.juraji.imagemanager.util.fxml.concurrent.ManagerTaskChain;

import java.util.Collection;

public class DefaultDirectoriesTask extends ManagerTaskChain<Directory, Void> {
    public DefaultDirectoriesTask(Collection<Directory> subjects) {
        super(subjects);

        this.nextTask(IndexDirectoryTask::new);
        this.nextTask(CorrectFileTypesTask::new);
        this.nextTask(HashDirectoryTask::new);
    }
}
