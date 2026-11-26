package com.dwelink.reservation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TerminerReservationRequest(
        @NotBlank(message = "Le code de retrait est obligatoire")
        String codeRetrait
) {
}
