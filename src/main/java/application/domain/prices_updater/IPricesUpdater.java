package application.domain.prices_updater;

import application.controllers.settings.dtos.UpdatePricesDTO;

public interface IPricesUpdater {
    void updatePrices(UpdatePricesDTO dto);
}
