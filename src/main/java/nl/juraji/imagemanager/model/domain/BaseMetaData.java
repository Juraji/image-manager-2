package nl.juraji.imagemanager.model.domain;

import nl.juraji.imagemanager.model.domain.hashes.HashData;

import javax.persistence.*;
import java.nio.file.Path;

/**
 * Created by Juraji on 25-11-2018.
 * Image Manager 2
 */
@MappedSuperclass
public abstract class BaseMetaData extends BaseModel {

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

    public Path getPath() {
        return path;
    }

    public void setPath(Path file) {
        this.path = file;
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

    public boolean isFileCorrected() {
        return fileCorrected;
    }

    public void setFileCorrected(boolean fileCorrected) {
        this.fileCorrected = fileCorrected;
    }
}
