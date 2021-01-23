package application.model.mappings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Api_Groceries.dbo.Mappings")
public class Mappings {

    @Column(name = "sCode", unique = true, nullable = false, length = 255)
    private String sCode;

    @Id
    @Column(name = "pName", unique = true, nullable = false, length = 255)
    private String pName;

    public String getsCode() {
        return sCode;
    }

    public void setsCode(String sCode) {
        this.sCode = sCode;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public Mappings(){}
    public Mappings(String name, String scode){
        this.pName = name;
        this.sCode = scode;
    }

    @Override
    public String toString() {
        return "Mappings{" +
                "sCode='" + sCode + '\'' +
                ", pName='" + pName + '\'' +
                '}';
    }
}
