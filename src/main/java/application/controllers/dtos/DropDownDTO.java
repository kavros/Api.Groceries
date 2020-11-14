package application.controllers.dtos;

public class DropDownDTO implements Comparable<DropDownDTO>{
    private String sCode;
    private String sName;

    public DropDownDTO(String sCode, String sName){
        this.sCode = sCode;
        this.sName = sName;
    }

    public String getsCode() {
        return sCode;
    }

    public void setsCode(String sCode) {
        this.sCode = sCode;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }
    @Override
    public int compareTo(DropDownDTO o) {
        return this.getsName().compareTo(o.getsName());
    }

}
