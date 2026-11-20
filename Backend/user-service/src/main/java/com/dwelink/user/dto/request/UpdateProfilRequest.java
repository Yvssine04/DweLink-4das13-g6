package com.dwelink.user.dto.request;

public class UpdateProfilRequest {
    private String prenom;
    private String nom;
    private String telephone;
    private String langue;
    private Integer rayonRechercheKm;

    public UpdateProfilRequest() {}

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getLangue() { return langue; }
    public void setLangue(String langue) { this.langue = langue; }

    public Integer getRayonRechercheKm() { return rayonRechercheKm; }
    public void setRayonRechercheKm(Integer rayonRechercheKm) { this.rayonRechercheKm = rayonRechercheKm; }
}
