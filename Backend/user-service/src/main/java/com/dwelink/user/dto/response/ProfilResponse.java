package com.dwelink.user.dto.response;

import java.time.LocalDateTime;

public class ProfilResponse {
    private Long id;
    private String email;
    private String prenom;
    private String nom;
    private String telephone;
    private String role;
    private String statut;
    private String langue;
    private Integer rayonRechercheKm;
    private LocalDateTime dateCreation;

    public ProfilResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getLangue() { return langue; }
    public void setLangue(String langue) { this.langue = langue; }

    public Integer getRayonRechercheKm() { return rayonRechercheKm; }
    public void setRayonRechercheKm(Integer rayonRechercheKm) { this.rayonRechercheKm = rayonRechercheKm; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
}
