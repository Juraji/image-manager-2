package nl.juraji.imagemanager.model.web.pinterest.types;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Juraji on 3-12-2018.
 * Image Manager 2
 */
public class BoardResource {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("url")
    private String url;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
