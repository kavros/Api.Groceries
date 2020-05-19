package application.model.smast;

import javax.persistence.*;

@Entity
@Table(name = "Cmp005.dbo.Smast")
public class Smast {

    @Id
    @Column(name = "sFileId", unique = true, nullable = false)
    private Integer sFileId;

    @Column(name = "sName", unique = false, nullable = true, length = 39)
    private String sName;

    @Column(name = "sCode", unique = false, nullable = false, length = 15)
    private String sCode;

    @Column(name = "sRetailPr", unique = false, nullable = true)
    private Float sRetailPr;

    public Float getsRetailPr() {
        return sRetailPr;
    }

    public void setsRetailPr(Float sRetailPr) {
        this.sRetailPr = sRetailPr;
    }


    public Integer getsFileId() {
        return sFileId;
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

    public void setsFileId(Integer sFileId) {
        this.sFileId = sFileId;
    }
}
