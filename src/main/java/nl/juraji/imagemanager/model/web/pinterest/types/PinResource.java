package nl.juraji.imagemanager.model.web.pinterest.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Juraji on 3-12-2018.
 * Image Manager 2
 */
@SuppressWarnings("unused")
public class PinResource {

    @SerializedName("id")
    private String id;

    @SerializedName("type")
    private String type;

    @SerializedName("link")
    private String link;

    // Set as JsonElement, since the Pinterest APi sometimes returns
    // an object with formatting options
    @SerializedName("title")
    private JsonElement title;

    @SerializedName("description")
    private String description;

    @SerializedName("images")
    private PinResourceImages images;

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getLink() {
        return link;
    }

    public String getTitle() {
        if (this.title != null) {
            final JsonPrimitive primitive = this.title.getAsJsonPrimitive();

            if (primitive.isString()) {
                return primitive.getAsString();
            }
        }

        return null;
    }

    public String getDescription() {
        return description;
    }

    public PinResourceImages getImages() {
        return images;
    }
}
