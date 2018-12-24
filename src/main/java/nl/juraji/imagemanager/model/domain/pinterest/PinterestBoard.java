package nl.juraji.imagemanager.model.domain.pinterest;

import nl.juraji.imagemanager.model.domain.BaseDirectory;
import nl.juraji.imagemanager.util.CollectionUtils;

import javax.persistence.*;
import java.net.URI;
import java.util.Set;

/**
 * Created by Juraji on 21-11-2018.
 * Image Manager 2
 */
@Entity
public class PinterestBoard extends BaseDirectory<PinterestBoard, PinMetaData> {

    @Column(nullable = false)
    private URI boardUrl;

    @Column(nullable = false)
    private String slug;

    @Column(nullable = false)
    private String boardId;

    @Column
    private BoardType type;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", fetch = FetchType.EAGER)
    private Set<PinterestBoard> children;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "board", fetch = FetchType.EAGER)
    private Set<PinMetaData> metaData;

    @ManyToOne
    private PinterestBoard parent;

    public PinterestBoard() {
        super("Pinterest");
        setFavorite(false);
    }

    public URI getBoardUrl() {
        return boardUrl;
    }

    public void setBoardUrl(URI boardUrl) {
        this.boardUrl = boardUrl;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public BoardType getType() {
        return type;
    }

    public void setType(BoardType type) {
        this.type = type;
    }

    @Override
    public Set<PinterestBoard> getChildren() {
        children = CollectionUtils.init(children);
        return children;
    }

    @Override
    public Set<PinMetaData> getMetaData() {
        metaData = CollectionUtils.init(metaData);
        return metaData;
    }

    @Override
    public PinterestBoard getParent() {
        return parent;
    }

    @Override
    public void setParent(PinterestBoard directory) {
        this.parent = directory;
    }

    @Override
    public Integer getMetaDataCount() {
        return this.metaData.size();
    }

    @Override
    public Integer getTotalMetaDataCount() {
        return this.metaData.size() + this.children.stream()
                .mapToInt(PinterestBoard::getTotalMetaDataCount)
                .sum();
    }
}
