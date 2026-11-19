package tn.dwelink.pharmacyservice.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import tn.dwelink.pharmacyservice.enums.JourSemaine;

public record HoraireRequest(
        @NotNull(message = "Le jour est obligatoire") JourSemaine jour,
        @NotNull(message = "L'heure d'ouverture est obligatoire") LocalTime heureOuverture,
        @NotNull(message = "L'heure de fermeture est obligatoire") LocalTime heureFermeture
) {
}
