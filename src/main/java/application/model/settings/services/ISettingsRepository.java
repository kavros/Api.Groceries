package application.model.settings.services;


import application.model.settings.Settings;

import java.util.Map;
import java.util.NoSuchElementException;

public interface ISettingsRepository {
    public Map<String,Settings> getAllSettings();
    public void add(Settings setting);
}
