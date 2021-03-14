package application.model.mapping.services;

import application.model.mapping.Mapping;
import java.util.List;

public interface IMappingsRepository {
    List getMappings();
    void saveMapping(Mapping mapping);
    void deleteMapping(Mapping mapping);
    void updateMapping(Mapping mapping);
}
