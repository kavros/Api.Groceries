package application.model.smast;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Cmp005.dbo.Smast")
public class Smast {

    @Id
    @Column(name = "sFileId", unique = true, nullable = false)
    private Integer sFileId;

    @Column(name = "sName", length = 39)
    private String sName;

    @Column(name = "sCode", nullable = false, length = 15)
    private String sCode;

    @Column(name = "sRetailPr")
    private BigDecimal sRetailPr;

    public float getsRetailPrice() {
        return sRetailPr.floatValue();
    }

    public void setsRetailPr(Float sRetailPr) {
        this.sRetailPr = new BigDecimal( sRetailPr.toString());
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
