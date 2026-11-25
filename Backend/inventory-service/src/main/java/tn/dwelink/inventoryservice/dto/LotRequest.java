package tn.dwelink.inventoryservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import tn.dwelink.inventoryservice.enums.OrigineLot;

public record LotRequest(
        @NotBlank(message = "Le numéro de lot est obligatoire") String numeroLot,
        @NotNull(message = "La date de péremption est obligatoire") LocalDate datePeremption,
        @NotNull @Min(value = 1, message = "La quantité doit être au moins 1") Integer quantite,
        OrigineLot origine   // facultatif : ACHAT par défaut
) {
}