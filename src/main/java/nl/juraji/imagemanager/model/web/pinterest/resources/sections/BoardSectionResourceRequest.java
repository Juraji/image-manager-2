package nl.juraji.imagemanager.model.web.pinterest.resources.sections;

import com.google.gson.annotations.SerializedName;
import nl.juraji.imagemanager.model.web.pinterest.resources.ResourceRequest;

/**
 * Created by Juraji on 9-12-2018.
 * Image Manager 2
 */
public class BoardSectionResourceRequest extends ResourceRequest<BoardSectionResourceResult> {

    @SerializedName("isPrefetch")
    private boolean isPrefetch;

    @SerializedName("board_slug")
    private String boardSlug;

    @SerializedName("section_slug")
    private String sectionSlug;

    @SerializedName("username")
    private String username;

    public BoardSectionResourceRequest(String username, String boardSlug, String sectionSlug) {
        super("/_ngjs/resource/BoardSectionResource/get/", BoardSectionResourceResult.class);

        this.isPrefetch = false;
        this.boardSlug = boardSlug;
        this.sectionSlug = sectionSlug;
        this.username = username;
    }
}
