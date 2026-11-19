package tn.dwelink.pharmacyservice.evenement;

import java.time.LocalDateTime;

/** Événement reçu de user-service quand le statut d'un compte utilisateur change. */
public record StatutCompteModifie(Long utilisateurId, String nouveauStatut, LocalDateTime date) {
}
