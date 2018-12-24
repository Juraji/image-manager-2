package nl.juraji.imagemanager.model.domain.local;

import nl.juraji.imagemanager.model.domain.BaseMetaData;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

/**
 * Created by Juraji on 24-11-2018.
 * Image Manager 2
 */
@Entity
public class LocalMetaData extends BaseMetaData {

    @ManyToOne(fetch = FetchType.LAZY)
    private LocalDirectory directory;

    public LocalDirectory getDirectory() {
        return directory;
    }

    public void setDirectory(LocalDirectory directory) {
        this.directory = directory;
    }
}
