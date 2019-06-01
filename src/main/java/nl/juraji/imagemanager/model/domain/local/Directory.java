package nl.juraji.imagemanager.model.domain.local;

import nl.juraji.imagemanager.model.domain.BaseModel;
import nl.juraji.imagemanager.util.CollectionUtils;

import javax.persistence.*;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Juraji on 24-11-2018.
 * Image Manager 2
 */
@Entity
public class Directory extends BaseModel {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 2048)
    private Path locationOnDisk;

    @Column
    private boolean favorite = false;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", fetch = FetchType.EAGER)
    private Set<Directory> children;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "directory", fetch = FetchType.EAGER)
    private Set<MetaData> metaData;

    @ManyToOne(fetch = FetchType.LAZY)
    private Directory parent;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Path getLocationOnDisk() {
        return locationOnDisk;
    }

    public void setLocationOnDisk(Path locationOnDisk) {
        this.locationOnDisk = locationOnDisk;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public Set<Directory> getChildren() {
        children = CollectionUtils.init(children);
        return children;
    }

    public Set<MetaData> getMetaData() {
        metaData = CollectionUtils.init(metaData);
        return metaData;
    }

    public void setMetaData(Set<MetaData> metaData) {
        this.metaData = metaData;
    }

    public Directory getParent() {
        return parent;
    }

    public void setParent(Directory parent) {
        this.parent = parent;
    }

    public Integer getMetaDataCount() {
        return this.metaData.size();
    }

    public Integer getTotalMetaDataCount() {
        return this.metaData.size() + this.children.stream()
                .mapToInt(Directory::getTotalMetaDataCount)
                .sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Directory directory = (Directory) o;
        return locationOnDisk.equals(directory.locationOnDisk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), locationOnDisk);
    }
}
