package application.domain.prices_updater;

import application.controllers.stepper.dto.UpdatePricesDTO;

public interface IPricesUpdater {
    void updatePrices(UpdatePricesDTO dto);
}
