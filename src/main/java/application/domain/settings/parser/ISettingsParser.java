package application.domain.settings.parser;


public interface ISettingsParser {
    public Float getProfit(String sCode);
    public Float getMinProfit(String sCode);
    public String getsCode(String sName) ;
}
