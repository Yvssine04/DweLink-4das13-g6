package com.dwelink.pharmacy.dto.request;

import jakarta.validation.constraints.NotNull;

public class DemandeMembreRequest {
    @NotNull(message = "L'ID utilisateur est obligatoire")
    private Long utilisateurId;

    private String role; // TITULAIRE, ASSISTANT (default: ASSISTANT)

    public DemandeMembreRequest() {}

    public Long getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(Long utilisateurId) { this.utilisateurId = utilisateurId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
