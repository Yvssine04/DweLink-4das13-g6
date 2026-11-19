package tn.dwelink.pharmacyservice.client;

/** Réponse de user-service : GET /api/utilisateurs/{id}. */
public record UtilisateurResponse(Long id, String role, String statut) {
}
