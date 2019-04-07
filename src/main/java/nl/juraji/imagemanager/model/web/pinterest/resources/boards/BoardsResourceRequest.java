package nl.juraji.imagemanager.model.web.pinterest.resources.boards;

import com.google.gson.annotations.SerializedName;
import nl.juraji.imagemanager.model.web.pinterest.resources.ResourceRequest;

/**
 * Created by Juraji on 4-12-2018.
 * Image Manager 2
 */
public class BoardsResourceRequest extends ResourceRequest<BoardsResourceResult> {

    @SerializedName("isPrefetch")
    private boolean isPrefetch;

    @SerializedName("privacy_filter")
    private String privacyFilter;

    @SerializedName("sort")
    private String sort;

    @SerializedName("field_set_key")
    private String fieldSetKey;

    @SerializedName("username")
    private String username;

    @SerializedName("page_size")
    private int pageSize;

    @SerializedName("group_by")
    private String groupBy;

    @SerializedName("include_archived")
    private boolean includeArchived;

    @SerializedName("redux_normalize_feed")
    private boolean reduxNormalizeFeed;

    public BoardsResourceRequest(String username) {
        super("/_ngjs/resource/BoardsResource/get/", BoardsResourceResult.class);
        this.username = username;
        this.isPrefetch = false;
        this.privacyFilter = "all";
        this.sort = "alphabetical";
        this.fieldSetKey = "profile_grid_item";
        this.pageSize = 500;
        this.groupBy = "mix_public_private";
        this.includeArchived = true;
        this.reduxNormalizeFeed = true;
    }
}
