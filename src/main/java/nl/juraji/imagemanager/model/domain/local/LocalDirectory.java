package nl.juraji.imagemanager.model.domain.local;

import nl.juraji.imagemanager.model.domain.BaseDirectory;
import nl.juraji.imagemanager.util.CollectionUtils;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by Juraji on 24-11-2018.
 * Image Manager 2
 */
@Entity
public class LocalDirectory extends BaseDirectory<LocalDirectory, LocalMetaData> {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", fetch = FetchType.EAGER)
    private Set<LocalDirectory> children;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "directory", fetch = FetchType.EAGER)
    private Set<LocalMetaData> metaData;

    @ManyToOne(fetch = FetchType.LAZY)
    private LocalDirectory parent;

    public LocalDirectory() {
        super("Local");
        setFavorite(false);
    }

    @Override
    public Set<LocalDirectory> getChildren() {
        children = CollectionUtils.init(children);
        return children;
    }

    @Override
    public Set<LocalMetaData> getMetaData() {
        metaData = CollectionUtils.init(metaData);
        return metaData;
    }

    @Override
    public LocalDirectory getParent() {
        return parent;
    }

    @Override
    public void setParent(LocalDirectory directory) {
        this.parent = directory;
    }

    @Override
    public Integer getMetaDataCount() {
        return this.metaData.size();
    }

    @Override
    public Integer getTotalMetaDataCount() {
        return this.metaData.size() + this.children.stream()
                .mapToInt(LocalDirectory::getTotalMetaDataCount)
                .sum();
    }
}
