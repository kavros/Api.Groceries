package application.controllers.settings.dtos;

import java.util.ArrayList;
import java.util.List;

public class MappingsDTO {
    String sName;
    String sCode;
    List<String> pNames;

    public MappingsDTO(){
        pNames= new ArrayList<>();
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getsCode() {
        return sCode;
    }

    public void setsCode(String sCode) {
        this.sCode = sCode;
    }

    public List<String> getpNames() {
        return pNames;
    }

    public void setpNames(List<String> pNames) {
        this.pNames = pNames;
    }
}
