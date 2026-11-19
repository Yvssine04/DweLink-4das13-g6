package tn.dwelink.catalogservice.dto;

import tn.dwelink.catalogservice.enums.ConditionConservation;
import tn.dwelink.catalogservice.enums.FormePharmaceutique;
import tn.dwelink.catalogservice.enums.StatutMedicament;

import java.time.LocalDate;
import java.util.List;

public record MedicamentResponse(
        Long id, String nomCommercial, String codeBarres, FormePharmaceutique forme,
        Boolean ordonnanceRequise, ConditionConservation conditionConservation,
        Boolean stupefiant, StatutMedicament statut, LocalDate dateRetrait,
        String categorie, String laboratoire, List<CompositionResponse> compositions) {}