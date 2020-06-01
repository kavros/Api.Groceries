package application.model.settings.services;


import java.util.NoSuchElementException;

public interface ISettingsRepository {
    public Float getProfit(String sCode) throws NoSuchElementException ;
    public Float getMinProfit(String sCode) throws NoSuchElementException ;
    public String getsCode(String sName) throws NoSuchElementException;
}
