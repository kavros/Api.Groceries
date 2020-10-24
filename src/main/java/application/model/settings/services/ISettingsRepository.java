package application.model.settings.services;

import application.model.settings.Settings;
import java.util.List;
import java.util.Map;

public interface ISettingsRepository {
    Map<String,Settings> getSnameToSettingMap();
    List<Settings> getSettings();
    void add(Settings setting);
}
