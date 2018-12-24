package nl.juraji.imagemanager.model.domain.pinterest;

import nl.juraji.imagemanager.model.domain.BaseMetaData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.net.URI;

/**
 * Created by Juraji on 21-11-2018.
 * Image Manager 2
 */
@Entity
public class PinMetaData extends BaseMetaData {

    @Column(nullable = false)
    private String pinId;

    @Column
    private String title;

    @Column(nullable = false, length = 2048)
    private URI pinterestUri;

    @Column(nullable = false, length = 2048)
    private URI downloadUrl;

    @Column(length = 2048)
    private URI sourceUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    private PinterestBoard board;

    public String getPinId() {
        return pinId;
    }

    public void setPinId(String pinId) {
        this.pinId = pinId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public URI getPinterestUri() {
        return pinterestUri;
    }

    public void setPinterestUri(URI pinterestUri) {
        this.pinterestUri = pinterestUri;
    }

    public URI getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(URI downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public URI getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(URI sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public PinterestBoard getBoard() {
        return board;
    }

    public void setBoard(PinterestBoard board) {
        this.board = board;
    }
}
