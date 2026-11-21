package com.dwelink.user.dto.response;

public class UserInternalResponse {
    private Long id;
    private String email;
    private String prenom;
    private String nom;
    private String role;
    private String statut;

    public UserInternalResponse() {}

    public UserInternalResponse(Long id, String email, String prenom, String nom, String role, String statut) {
        this.id = id;
        this.email = email;
        this.prenom = prenom;
        this.nom = nom;
        this.role = role;
        this.statut = statut;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}
