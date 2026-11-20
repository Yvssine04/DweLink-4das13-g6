package com.dwelink.pharmacy.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pharmacie")
public class Pharmacie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(name = "numero_autorisation", nullable = false, unique = true, length = 100)
    private String numeroAutorisation;

    @Column(nullable = false)
    private String adresse;

    @Column(nullable = false, length = 100)
    private String ville;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false, length = 50)
    private String telephone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StatutPharmacie statut;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @OneToMany(mappedBy = "pharmacie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MembrePharmacie> membres = new ArrayList<>();

    @OneToMany(mappedBy = "pharmacie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Horaire> horaires = new ArrayList<>();

    @OneToMany(mappedBy = "pharmacie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Garde> gardes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
        if (this.statut == null) {
            this.statut = StatutPharmacie.EN_ATTENTE;
        }
    }

    public Pharmacie() {}

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

    public StatutPharmacie getStatut() { return statut; }
    public void setStatut(StatutPharmacie statut) { this.statut = statut; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public List<MembrePharmacie> getMembres() { return membres; }
    public void setMembres(List<MembrePharmacie> membres) { this.membres = membres; }

    public List<Horaire> getHoraires() { return horaires; }
    public void setHoraires(List<Horaire> horaires) { this.horaires = horaires; }

    public List<Garde> getGardes() { return gardes; }
    public void setGardes(List<Garde> gardes) { this.gardes = gardes; }
}
