package nl.juraji.imagemanager.model.web.pinterest.resources.boards;

import com.google.gson.annotations.SerializedName;
import nl.juraji.imagemanager.model.web.pinterest.resources.ResourceRequest;

/**
 * Created by Juraji on 9-12-2018.
 * Image Manager 2
 */
public class BoardPageResourceRequest extends ResourceRequest<BoardPageResourceResult> {

    @SerializedName("isPrefetch")
    private boolean isPrefetch;

    @SerializedName("username")
    private String username;

    @SerializedName("slug")
    private String slug;

    @SerializedName("field_set_key")
    private String fieldSetKey;

    @SerializedName("main_module_name")
    private String mainModuleName;

    public BoardPageResourceRequest(String username, String boardSlug) {
        super("/resource/BoardPageResource/get/", BoardPageResourceResult.class);
        this.isPrefetch = false;
        this.username = username;
        this.slug = boardSlug;
        this.fieldSetKey = "detailed";
        this.mainModuleName = "BoardPage";
    }
}
