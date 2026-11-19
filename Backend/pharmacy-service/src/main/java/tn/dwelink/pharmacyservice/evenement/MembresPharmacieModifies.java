package tn.dwelink.pharmacyservice.evenement;

import java.time.LocalDateTime;

public record MembresPharmacieModifies(
        Long pharmacieId,
        Long membreId,
        Long utilisateurId,
        TypeChangementMembre changement,
        LocalDateTime date) {
}
