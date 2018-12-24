package nl.juraji.imagemanager.model.web.pinterest.types;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Juraji on 3-12-2018.
 * Image Manager 2
 */
@SuppressWarnings("unused")
public class BoardSectionResource {

    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("slug")
    private String slug;

    @SerializedName("pin_count")
    private int pinCount;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSlug() {
        return slug;
    }

    public int getPinCount() {
        return pinCount;
    }
}
