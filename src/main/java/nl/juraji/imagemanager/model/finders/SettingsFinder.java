package nl.juraji.imagemanager.model.finders;

import io.ebean.Finder;
import nl.juraji.imagemanager.model.domain.settings.Settings;

import java.util.List;
import java.util.UUID;

/**
 * Created by Juraji on 27-11-2018.
 * Image Manager 2
 */
public final class SettingsFinder extends Finder<UUID, Settings> {
    private static final SettingsFinder INSTANCE = new SettingsFinder();

    private SettingsFinder() {
        super(Settings.class);
    }

    public static Settings getSettings() {
        return INSTANCE.settings();
    }

    private Settings settings() {
        final List<Settings> all = this.all();
        if (all.isEmpty()) {
            return this.initSettings();
        } else {
            return all.get(0);
        }
    }

    private Settings initSettings() {
        final Settings settings = new Settings();
        settings.setDuplicateScannerMinSimilarity(82);

        settings.save();
        return settings;
    }
}
