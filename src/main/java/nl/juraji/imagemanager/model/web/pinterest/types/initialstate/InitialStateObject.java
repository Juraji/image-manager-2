package nl.juraji.imagemanager.model.web.pinterest.types.initialstate;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Juraji on 2-5-2019.
 * image-manager
 *
 * This object, and its descendants are a partial of the fully available content!
 */
@SuppressWarnings("unused")
public class InitialStateObject {

    @SerializedName("viewer")
    private InitialStateObjectViewer viewer;

    public InitialStateObjectViewer getViewer() {
        return viewer;
    }
}
