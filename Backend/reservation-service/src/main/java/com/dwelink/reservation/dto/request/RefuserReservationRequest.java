package com.dwelink.reservation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefuserReservationRequest(
        @NotBlank(message = "Le motif est obligatoire")
        String motif
) {
}
