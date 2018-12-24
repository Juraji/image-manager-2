package nl.juraji.imagemanager.util.fxml;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.stage.Window;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by Juraji on 23-11-2018.
 * Image Manager 2
 */
public final class FXMLUtils {
    private static final Image APPLICATION_ICON = new Image("/nl/juraji/imagemanager/images/application.png");

    private FXMLUtils() {
    }

    public static Image getApplicationIcon() {
        return APPLICATION_ICON;
    }

    public static void centerOnParent(Dialog child, Window parent) {
        // This kind of magic is necessary Since Dialog does not implement Window,
        // but does have the same code to center on a parent
        centerOnParentEx(parent, child::getWidth, child::getHeight, child::isShowing,
                child::setX, child::setY, child::setOnShown);
    }

    public static void centerOnParent(Window child, Window parent) {
        // This kind of magic is necessary Since Dialog does not implement Window,
        // but does have the same code to center on a parent
        centerOnParentEx(parent, child::getWidth, child::getHeight, child::isShowing,
                child::setX, child::setY, child::setOnShown);
    }

    private static <EV extends Event> void centerOnParentEx(
            Window parent,
            Supplier<Double> childWidthSupplier,
            Supplier<Double> childHeightSupplier,
            Supplier<Boolean> childIsShowingSupplier,
            Consumer<Double> childXSetter,
            Consumer<Double> childYSetter,
            Consumer<EventHandler<EV>> childOnShownEventHandlerSetter) {

        Runnable calculateAndSetCoordinates = () -> {
            // parent center coordinates
            final double centerX = parent.getX() + (parent.getWidth() / 2d);
            final double centerY = parent.getY() + (parent.getHeight() / 2d);
            childXSetter.accept(centerX - (childWidthSupplier.get() / 2d));
            childYSetter.accept(centerY - (childHeightSupplier.get() / 2d));
        };

        // If child is already showing apply immediately,
        // else delay until shown
        if (childIsShowingSupplier.get()) {
            calculateAndSetCoordinates.run();
        } else {
            childOnShownEventHandlerSetter.accept(e -> calculateAndSetCoordinates.run());
        }
    }
}
