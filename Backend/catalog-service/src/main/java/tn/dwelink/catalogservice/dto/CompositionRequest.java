package tn.dwelink.catalogservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;

public record CompositionRequest(
        @NotNull Long principeActifId,
        @NotNull @Positive Double dosage,
        @NotBlank String unite) {}