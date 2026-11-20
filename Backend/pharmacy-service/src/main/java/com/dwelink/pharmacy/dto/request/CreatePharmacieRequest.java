package com.dwelink.pharmacy.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreatePharmacieRequest {
    @NotNull(message = "L'ID de l'utilisateur pharmacien titulaire est obligatoire")
    private Long titulaireId;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le numéro d'autorisation est obligatoire")
    private String numeroAutorisation;

    @NotBlank(message = "L'adresse est obligatoire")
    private String adresse;

    @NotBlank(message = "La ville est obligatoire")
    private String ville;

    @NotNull(message = "La latitude est obligatoire")
    private Double latitude;

    @NotNull(message = "La longitude est obligatoire")
    private Double longitude;

    @NotBlank(message = "Le téléphone est obligatoire")
    private String telephone;

    public CreatePharmacieRequest() {}

    public Long getTitulaireId() { return titulaireId; }
    public void setTitulaireId(Long titulaireId) { this.titulaireId = titulaireId; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getNumeroAutorisation() { return numeroAutorisation; }
    public void setNumeroAutorisation(String numeroAutorisation) { this.numeroAutorisation = numeroAutorisation; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
}
