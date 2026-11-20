package com.dwelink.pharmacy.dto.response;

import java.time.LocalDateTime;

public class MembrePharmacieResponse {
    private Long id;
    private Long pharmacieId;
    private Long utilisateurId;
    private String role;
    private String statut;
    private LocalDateTime dateCreation;

    public MembrePharmacieResponse() {}

    public MembrePharmacieResponse(Long id, Long pharmacieId, Long utilisateurId, String role, String statut, LocalDateTime dateCreation) {
        this.id = id;
        this.pharmacieId = pharmacieId;
        this.utilisateurId = utilisateurId;
        this.role = role;
        this.statut = statut;
        this.dateCreation = dateCreation;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPharmacieId() { return pharmacieId; }
    public void setPharmacieId(Long pharmacieId) { this.pharmacieId = pharmacieId; }

    public Long getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(Long utilisateurId) { this.utilisateurId = utilisateurId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
}
