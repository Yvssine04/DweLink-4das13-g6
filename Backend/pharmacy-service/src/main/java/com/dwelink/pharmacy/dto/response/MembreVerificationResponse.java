package com.dwelink.pharmacy.dto.response;

public class MembreVerificationResponse {
    private Long pharmacieId;
    private Long utilisateurId;
    private boolean estMembreActif;
    private String role;

    public MembreVerificationResponse() {}

    public MembreVerificationResponse(Long pharmacieId, Long utilisateurId, boolean estMembreActif, String role) {
        this.pharmacieId = pharmacieId;
        this.utilisateurId = utilisateurId;
        this.estMembreActif = estMembreActif;
        this.role = role;
    }

    public Long getPharmacieId() { return pharmacieId; }
    public void setPharmacieId(Long pharmacieId) { this.pharmacieId = pharmacieId; }

    public Long getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(Long utilisateurId) { this.utilisateurId = utilisateurId; }

    public boolean isEstMembreActif() { return estMembreActif; }
    public void setEstMembreActif(boolean estMembreActif) { this.estMembreActif = estMembreActif; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
