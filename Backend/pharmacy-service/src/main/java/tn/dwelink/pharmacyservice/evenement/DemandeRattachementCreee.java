package tn.dwelink.pharmacyservice.evenement;

import java.time.LocalDateTime;

public record DemandeRattachementCreee(
        Long pharmacieId,
        String nomPharmacie,
        Long membreId,
        Long utilisateurId,
        LocalDateTime date) {
}
