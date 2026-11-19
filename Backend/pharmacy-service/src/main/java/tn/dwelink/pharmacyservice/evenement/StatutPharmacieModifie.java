package tn.dwelink.pharmacyservice.evenement;

import java.time.LocalDateTime;
import tn.dwelink.pharmacyservice.enums.StatutPharmacie;

public record StatutPharmacieModifie(
        Long pharmacieId,
        String nom,
        StatutPharmacie ancienStatut,
        StatutPharmacie nouveauStatut,
        LocalDateTime date) {
}
