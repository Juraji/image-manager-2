package nl.juraji.imagemanager.model.web.pinterest.resources.pins;

import com.google.gson.annotations.SerializedName;
import nl.juraji.imagemanager.model.domain.pinterest.BoardType;
import nl.juraji.imagemanager.model.domain.pinterest.PinMetaData;
import nl.juraji.imagemanager.model.domain.pinterest.PinterestBoard;
import nl.juraji.imagemanager.model.web.pinterest.resources.ResourceRequest;
import org.openqa.selenium.remote.http.HttpMethod;

import java.net.URI;

/**
 * Created by Juraji on 8-12-2018.
 * Image Manager 2
 */
public class MovePinResourceRequest extends ResourceRequest<MovePinResourceResult> {

    @SerializedName("board_id")
    private String boardId;

    @SerializedName("board_section_id")
    private String boardSectionId;

    @SerializedName("description")
    private String description;

    @SerializedName("disable_comments")
    private boolean disableComments;

    @SerializedName("disable_did_it")
    private boolean disableDidIt;

    @SerializedName("id")
    private String id;

    @SerializedName("link")
    private String link;

    @SerializedName("title")
    private String title;

    public MovePinResourceRequest(PinMetaData metaData, PinterestBoard target, String CSRFToken) {
        super(HttpMethod.POST, "/resource/PinResource/update/", MovePinResourceResult.class);
        super.getHeaders().put("X-CSRFToken", CSRFToken);

        // Set boardId to board id if is board
        if (BoardType.BOARD.equals(target.getType())) {
            this.boardId = target.getBoardId();
        } else {
            // Set boardId to board id to parent board if is section
            this.boardId = target.getParent().getBoardId();
            this.boardSectionId = target.getBoardId();
        }

        this.title = metaData.getTitle();
        this.description = metaData.getComments();
        this.disableComments = false;
        this.disableDidIt = false;
        this.id = metaData.getPinId();

        final URI sourceUrl = metaData.getSourceUrl();
        this.link = sourceUrl != null ? sourceUrl.toString() : null;
    }
}
