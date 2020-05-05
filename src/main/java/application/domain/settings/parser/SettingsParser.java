package application.domain.settings.parser;

import application.model.settings.Settings;
import org.springframework.stereotype.Component;

@Component("settingsParser")
public class SettingsParser implements ISettingsParser {

    @Override
    public Settings getSettings() {
        return null;
    }
}
