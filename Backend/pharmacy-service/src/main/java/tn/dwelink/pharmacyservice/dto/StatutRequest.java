package tn.dwelink.pharmacyservice.dto;

import jakarta.validation.constraints.NotNull;
import tn.dwelink.pharmacyservice.enums.StatutPharmacie;

public record StatutRequest(@NotNull(message = "Le statut est obligatoire") StatutPharmacie statut) {
}
