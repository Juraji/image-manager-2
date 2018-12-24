package nl.juraji.imagemanager.model.web.pinterest.types;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Juraji on 3-12-2018.
 * Image Manager 2
 */
@SuppressWarnings("unused")
public class PinResourceImage {

    @SerializedName("url")
    private String url;

    @SerializedName("width")
    private int width;

    @SerializedName("height")
    private int height;

    public String getUrl() {
        return url;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
