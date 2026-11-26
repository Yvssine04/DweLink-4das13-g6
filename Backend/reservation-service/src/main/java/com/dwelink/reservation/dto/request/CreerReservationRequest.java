package com.dwelink.reservation.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record CreerReservationRequest(
        @NotNull(message = "L'identifiant de la pharmacie est obligatoire")
        @Positive(message = "L'identifiant de la pharmacie est invalide")
        Long pharmacieId,

        @NotEmpty(message = "Au moins une ligne est obligatoire")
        @Valid
        List<LigneReservationRequest> lignes
) {
}
