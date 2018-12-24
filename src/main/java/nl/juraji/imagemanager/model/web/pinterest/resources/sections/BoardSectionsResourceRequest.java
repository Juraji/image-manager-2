package nl.juraji.imagemanager.model.web.pinterest.resources.sections;

import com.google.gson.annotations.SerializedName;
import nl.juraji.imagemanager.model.web.pinterest.resources.ResourceRequest;

/**
 * Created by Juraji on 4-12-2018.
 * Image Manager 2
 */
public class BoardSectionsResourceRequest extends ResourceRequest<BoardSectionsResourceResult> {

    @SerializedName("isPrefetch")
    private boolean isPrefetch;

    @SerializedName("board_id")
    private String boardId;

    @SerializedName("redux_normalize_feed")
    private boolean reduxNormalizeFeed;

    public BoardSectionsResourceRequest(String boardId) {
        super("/_ngjs/resource/BoardSectionsResource/get/", BoardSectionsResourceResult.class);
        this.boardId = boardId;
        this.isPrefetch = false;
        this.reduxNormalizeFeed = true;
    }
}
