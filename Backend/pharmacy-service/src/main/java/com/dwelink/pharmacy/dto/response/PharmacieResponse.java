package com.dwelink.pharmacy.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class PharmacieResponse {
    private Long id;
    private String nom;
    private String numeroAutorisation;
    private String adresse;
    private String ville;
    private Double latitude;
    private Double longitude;
    private String telephone;
    private String statut;
    private LocalDateTime dateCreation;
    private List<MembrePharmacieResponse> membres;
    private List<HoraireResponse> horaires;
    private List<GardeResponse> gardes;

    public PharmacieResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public List<MembrePharmacieResponse> getMembres() { return membres; }
    public void setMembres(List<MembrePharmacieResponse> membres) { this.membres = membres; }

    public List<HoraireResponse> getHoraires() { return horaires; }
    public void setHoraires(List<HoraireResponse> horaires) { this.horaires = horaires; }

    public List<GardeResponse> getGardes() { return gardes; }
    public void setGardes(List<GardeResponse> gardes) { this.gardes = gardes; }
}
