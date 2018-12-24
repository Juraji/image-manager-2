package nl.juraji.imagemanager.model.web.pinterest.resources.pins;

import com.google.gson.annotations.SerializedName;
import nl.juraji.imagemanager.model.web.pinterest.resources.ResourceRequest;
import org.openqa.selenium.remote.http.HttpMethod;

/**
 * Created by Juraji on 5-12-2018.
 * Image Manager 2
 */
public class DeletePinResourceRequest extends ResourceRequest<DeletePinResourceResult> {

    @SerializedName("id")
    private String pinId;

    public DeletePinResourceRequest(String pinId, String CSRFToken) {
        super(HttpMethod.POST, "/resource/PinResource/delete/", DeletePinResourceResult.class);
        this.pinId = pinId;
        super.getHeaders().put("X-CSRFToken", CSRFToken);
    }
}
