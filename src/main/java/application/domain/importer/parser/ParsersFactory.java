package application.domain.importer.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ParsersFactory {
    @Autowired
    private List<IParsers> services;

    private static final Map<String, IParsers> myServiceCache = new HashMap<>();

    @PostConstruct
    public void initMyServiceCache() {
        for(IParsers service : services) {
            myServiceCache.put(service.getType(), service);
        }
    }

    public static IParsers getService(String type) {
        IParsers service = myServiceCache.get(type);
        if(service == null) throw new RuntimeException("Unknown service type: " + type);
        return service;
    }
}
