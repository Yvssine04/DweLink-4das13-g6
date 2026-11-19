package tn.dwelink.pharmacyservice.dto;

import jakarta.validation.constraints.NotNull;
import tn.dwelink.pharmacyservice.enums.StatutMembre;

public record DecisionMembreRequest(@NotNull(message = "Le statut est obligatoire") StatutMembre statut) {
}
