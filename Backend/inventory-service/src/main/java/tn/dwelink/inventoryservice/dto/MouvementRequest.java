package tn.dwelink.inventoryservice.dto;

import jakarta.validation.constraints.NotNull;
import tn.dwelink.inventoryservice.enums.TypeMouvement;

public record MouvementRequest(
        @NotNull(message = "Le type est obligatoire") TypeMouvement type,
        @NotNull(message = "La quantité est obligatoire") Integer quantite,
        String lotId,   // obligatoire seulement pour un AJUSTEMENT
        String motif
) {
}