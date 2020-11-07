package application.model.mappings.services;

import application.hibernate.HibernateUtil;
import org.springframework.stereotype.Component;
import java.util.List;

@Component("mappingsRepository")
public class MappingsRepository implements IMappingsRepository {
    @Override
    public List getMappings() {
        return HibernateUtil.getElements("Mappings");
    }
}
