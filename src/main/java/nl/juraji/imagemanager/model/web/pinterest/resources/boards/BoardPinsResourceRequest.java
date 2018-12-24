package nl.juraji.imagemanager.model.web.pinterest.resources.boards;

import com.google.gson.annotations.SerializedName;
import nl.juraji.imagemanager.model.web.pinterest.resources.ResourceRequest;

import java.util.ArrayList;

/**
 * Created by Juraji on 4-12-2018.
 * Image Manager 2
 */
public class BoardPinsResourceRequest extends ResourceRequest<BoardPinsResourceResult> {

    @SerializedName("bookmarks")
    private ArrayList<String> bookmarks;

    @SerializedName("isPrefetch")
    private boolean isPrefetch;

    @SerializedName("board_id")
    private String boardId;

    @SerializedName("field_set_key")
    private String fieldSetKey;

    @SerializedName("filter_section_pins")
    private boolean filterSectionPins;

    @SerializedName("layout")
    private String layout;

    @SerializedName("page_size")
    private int pageSize;

    @SerializedName("redux_normalize_feed")
    private boolean reduxNormalizeFeed;

    public BoardPinsResourceRequest(String boardId, String bookmark) {
        super("/resource/BoardFeedResource/get/", BoardPinsResourceResult.class);

        if (bookmark != null) {
            this.bookmarks = new ArrayList<>();
            this.bookmarks.add(bookmark);
        }

        this.boardId = boardId;
        this.isPrefetch = true;
        this.fieldSetKey = "react_grid_pin";
        this.filterSectionPins = true;
        this.layout = "default";
        this.pageSize = 100;
        this.reduxNormalizeFeed = true;
    }
}
