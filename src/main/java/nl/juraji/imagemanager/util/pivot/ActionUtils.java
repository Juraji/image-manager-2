package nl.juraji.imagemanager.util.pivot;

import org.apache.pivot.wtk.Action;
import org.apache.pivot.wtk.Component;

/**
 * Created by Juraji on 14-5-2019.
 * image-manager
 */
public final class ActionUtils {
    private ActionUtils() {
        // Private constructor
    }

    public static void putNamedAction(String id, Runnable action) {
        Action.getNamedActions().put(id, new Action() {
            @Override
            public void perform(Component source) {
                action.run();
            }
        });
    }
}
