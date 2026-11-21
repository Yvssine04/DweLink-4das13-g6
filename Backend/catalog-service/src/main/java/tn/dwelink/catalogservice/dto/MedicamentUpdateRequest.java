package tn.dwelink.catalogservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tn.dwelink.catalogservice.enums.ConditionConservation;
import tn.dwelink.catalogservice.enums.FormePharmaceutique;

public record MedicamentUpdateRequest(
        @NotBlank String nomCommercial,
        @NotBlank String codeBarres,
        @NotNull FormePharmaceutique forme,
        Boolean ordonnanceRequise,
        ConditionConservation conditionConservation,
        Boolean stupefiant,
        String categorie,
        String laboratoire) {}