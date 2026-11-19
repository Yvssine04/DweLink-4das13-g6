package tn.dwelink.pharmacyservice.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record GardeRequest(
        @NotNull(message = "La date de début est obligatoire") LocalDateTime debut,
        @NotNull(message = "La date de fin est obligatoire") LocalDateTime fin
) {
}
