package nl.juraji.imagemanager.model.web.pinterest.types;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Juraji on 3-12-2018.
 * Image Manager 2
 */
@SuppressWarnings("unused")
public class PinResourceImages {

    @SerializedName("orig")
    private PinResourceImage original;

    public PinResourceImage getOriginal() {
        return original;
    }
}
