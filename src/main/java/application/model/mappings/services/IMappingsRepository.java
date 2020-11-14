package application.model.mappings.services;

import application.model.mappings.Mappings;
import java.util.List;

public interface IMappingsRepository {
    List getMappings();
    void saveMapping(Mappings mapping);
}
