package nl.juraji.imagemanager.model.domain.local;

import nl.juraji.imagemanager.model.domain.BaseModel;
import nl.juraji.imagemanager.model.domain.hashes.HashData;

import javax.persistence.*;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Created by Juraji on 24-11-2018.
 * Image Manager 2
 */
@Entity
public class MetaData extends BaseModel {

    @Column(length = 2048)
    private Path path;

    @Column
    private long qualityRating = 0;

    @Column
    private long fileSize = 0;

    @Column
    private int width = 0;

    @Column
    private int height = 0;

    @Column
    private boolean fileCorrected = false;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private HashData hash;

    @ManyToOne(fetch = FetchType.LAZY)
    private Directory directory;

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public long getQualityRating() {
        return qualityRating;
    }

    public void setQualityRating(long qualityRating) {
        this.qualityRating = qualityRating;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isFileCorrected() {
        return fileCorrected;
    }

    public void setFileCorrected(boolean fileCorrected) {
        this.fileCorrected = fileCorrected;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public HashData getHash() {
        return hash;
    }

    public void setHash(HashData hash) {
        this.hash = hash;
    }

    public Directory getDirectory() {
        return directory;
    }

    public void setDirectory(Directory directory) {
        this.directory = directory;
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
        MetaData metaData = (MetaData) o;
        return path.equals(metaData.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), path);
    }
}
