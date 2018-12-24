package nl.juraji.imagemanager.model.web.pinterest.types;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Juraji on 9-12-2018.
 * Image Manager 2
 */
public class BoardPageResource {

    @SerializedName("follower_count")
    private int followerCount;

    @SerializedName("pin_count")
    private int pinCount;

    @SerializedName("sectionless_pin_count")
    private int sectionlessPinCount;

    public int getFollowerCount() {
        return followerCount;
    }

    public int getPinCount() {
        return pinCount;
    }

    public int getSectionlessPinCount() {
        return sectionlessPinCount;
    }
}
