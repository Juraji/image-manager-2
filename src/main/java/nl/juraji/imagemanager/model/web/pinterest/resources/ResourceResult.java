package nl.juraji.imagemanager.model.web.pinterest.resources;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Juraji on 3-12-2018.
 * Image Manager 2
 */
@SuppressWarnings("unused")
public abstract class ResourceResult<T> {

    @SerializedName("status")
    private int status;

    @SerializedName("bookmark")
    private String bookmark;

    @SerializedName("data")
    private T data;

    public int getStatus() {
        return status;
    }

    public String getBookmark() {
        return bookmark;
    }

    public T getData() {
        return data;
    }
}
