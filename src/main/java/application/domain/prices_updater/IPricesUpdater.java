package application.domain.prices_updater;

import application.controllers.dtos.UpdatePricesDTO;

public interface IPricesUpdater {
    void updatePrices(UpdatePricesDTO dto);
}
