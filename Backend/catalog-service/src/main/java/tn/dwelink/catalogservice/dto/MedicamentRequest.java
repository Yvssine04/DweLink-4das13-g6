package tn.dwelink.catalogservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import tn.dwelink.catalogservice.enums.ConditionConservation;
import tn.dwelink.catalogservice.enums.FormePharmaceutique;

import java.util.List;

public record MedicamentRequest(
        @NotBlank String nomCommercial,
        @NotBlank String codeBarres,
        @NotNull FormePharmaceutique forme,
        Boolean ordonnanceRequise,
        ConditionConservation conditionConservation,
        Boolean stupefiant,
        String categorie,
        String laboratoire,
        @NotEmpty @Valid List<CompositionRequest> compositions) {}
