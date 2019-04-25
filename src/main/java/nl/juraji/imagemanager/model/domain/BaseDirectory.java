package nl.juraji.imagemanager.model.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.nio.file.Path;
import java.util.Set;

/**
 * Created by Juraji on 25-11-2018.
 * Image Manager 2
 */
@MappedSuperclass
public abstract class BaseDirectory<T extends BaseDirectory<T, U>, U extends BaseMetaData> extends BaseModel {

    @Transient
    private final String origin;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 2048)
    private Path locationOnDisk;

    @Column
    private Boolean favorite;

    protected BaseDirectory(String origin) {
        this.origin = origin;
    }

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

    public Boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getOrigin() {
        return origin;
    }

    // Implement property in sub-class
    public abstract Set<T> getChildren();

    // Implement property in sub-class
    public abstract Set<U> getMetaData();

    // Implement property in sub-class
    public abstract T getParent();

    // Implement property in sub-class
    public abstract void setParent(T directory);

    // Implement logic in sub-class
    public abstract Integer getMetaDataCount();

    // Implement logic in sub-class
    public abstract Integer getTotalMetaDataCount();
}
