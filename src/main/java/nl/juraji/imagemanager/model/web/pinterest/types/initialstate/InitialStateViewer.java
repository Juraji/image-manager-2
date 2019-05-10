package nl.juraji.imagemanager.model.web.pinterest.types.initialstate;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Juraji on 2-5-2019.
 * image-manager
 */
public class InitialStateViewer {

    @SerializedName("id")
    private String id;

    @SerializedName("email")
    private String email;

    @SerializedName("username")
    private String username;

    @SerializedName("fullName")
    private String fullName;

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }
}
