package com.dwelink.reservation.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record LigneReservationRequest(
        @NotNull(message = "L'identifiant du médicament est obligatoire")
        @Positive(message = "L'identifiant du médicament est invalide")
        Long medicamentId,

        @NotBlank(message = "Le nom du médicament est obligatoire")
        String nomMedicament,

        @NotNull(message = "La quantité est obligatoire")
        @Positive(message = "La quantité doit être supérieure à 0")
        Integer quantite,

        @NotNull(message = "Le prix unitaire est obligatoire")
        @DecimalMin(value = "0.0", message = "Le prix unitaire ne peut pas être négatif")
        BigDecimal prixUnitaire,

        @NotNull(message = "Le champ ordonnanceRequise est obligatoire")
        Boolean ordonnanceRequise
) {
}
