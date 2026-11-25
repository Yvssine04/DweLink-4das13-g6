package tn.dwelink.inventoryservice.dto;

import jakarta.validation.constraints.NotNull;
import tn.dwelink.inventoryservice.enums.StatutLot;

public record StatutLotRequest(
        @NotNull(message = "Le statut est obligatoire") StatutLot statut
) {
}