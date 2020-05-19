package application.domain.settings.repo;


public interface ISettingsRepository {
    public Float getProfit(String sCode);
    public Float getMinProfit(String sCode);
    public String getsCode(String sName) ;
}
