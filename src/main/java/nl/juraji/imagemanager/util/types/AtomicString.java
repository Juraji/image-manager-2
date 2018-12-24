package nl.juraji.imagemanager.util.types;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Juraji on 29-11-2018.
 * Image Manager 2
 */
public class AtomicString extends AtomicReference<String> {
    public boolean equals(String otherString) {
        final String thisString = this.get();
        return thisString != null
                && otherString != null
                && thisString.equals(otherString);
    }
}
