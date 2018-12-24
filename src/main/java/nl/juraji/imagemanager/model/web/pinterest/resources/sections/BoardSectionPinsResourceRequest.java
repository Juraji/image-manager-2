package nl.juraji.imagemanager.model.web.pinterest.resources.sections;

import com.google.gson.annotations.SerializedName;
import nl.juraji.imagemanager.model.web.pinterest.resources.ResourceRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Juraji on 4-12-2018.
 * Image Manager 2
 */
public class BoardSectionPinsResourceRequest extends ResourceRequest<BoardSectionPinsResourceResult> {

    @SerializedName("bookmarks")
    public List<String> bookmarks;

    @SerializedName("isPrefetch")
    public boolean isPrefetch;

    @SerializedName("field_set_key")
    public String fieldSetKey;

    @SerializedName("is_own_profile_pins")
    public boolean isOwnProfilePins;

    @SerializedName("page_size")
    public int pageSize;

    @SerializedName("redux_normalize_feed")
    public boolean reduxNormalizeFeed;

    @SerializedName("section_id")
    public String sectionId;

    public BoardSectionPinsResourceRequest(String sectionId, String bookmark) {
        super("/_ngjs/resource/BoardSectionPinsResource/get/", BoardSectionPinsResourceResult.class);

        if (bookmark != null) {
            this.bookmarks = new ArrayList<>();
            this.bookmarks.add(bookmark);
        }

        this.sectionId = sectionId;
        this.isPrefetch = true;
        this.fieldSetKey = "react_grid_pin";
        this.isOwnProfilePins = true;
        this.pageSize = 100;
        this.reduxNormalizeFeed = true;
    }
}
