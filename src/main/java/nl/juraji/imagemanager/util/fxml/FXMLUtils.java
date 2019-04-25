package nl.juraji.imagemanager.util.fxml;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.stage.Window;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

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

    private static <E extends Event> void centerOnParentEx(
            Window parent,
            DoubleSupplier childWidthSupplier,
            DoubleSupplier childHeightSupplier,
            BooleanSupplier childIsShowingSupplier,
            DoubleConsumer childXSetter,
            DoubleConsumer childYSetter,
            Consumer<EventHandler<E>> childOnShownEventHandlerSetter) {

        Runnable calculateAndSetCoordinates = () -> {
            // parent center coordinates
            final double centerX = parent.getX() + (parent.getWidth() / 2d);
            final double centerY = parent.getY() + (parent.getHeight() / 2d);
            childXSetter.accept(centerX - (childWidthSupplier.getAsDouble() / 2d));
            childYSetter.accept(centerY - (childHeightSupplier.getAsDouble() / 2d));
        };

        // If child is already showing apply immediately,
        // else delay until shown
        if (childIsShowingSupplier.getAsBoolean()) {
            calculateAndSetCoordinates.run();
        } else {
            childOnShownEventHandlerSetter.accept(e -> calculateAndSetCoordinates.run());
        }
    }
}
