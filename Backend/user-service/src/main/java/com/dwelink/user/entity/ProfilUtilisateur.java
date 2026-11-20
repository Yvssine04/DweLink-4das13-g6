package com.dwelink.user.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "profil_utilisateur")
public class ProfilUtilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(length = 50)
    private String telephone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private RoleUtilisateur role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StatutCompte statut;

    @Column(length = 10)
    private String langue = "fr";

    @Column(name = "rayon_recherche_km")
    private Integer rayonRechercheKm = 10;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DemandePharmacien> demandes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.dateModification = LocalDateTime.now();
    }

    public ProfilUtilisateur() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public RoleUtilisateur getRole() { return role; }
    public void setRole(RoleUtilisateur role) { this.role = role; }

    public StatutCompte getStatut() { return statut; }
    public void setStatut(StatutCompte statut) { this.statut = statut; }

    public String getLangue() { return langue; }
    public void setLangue(String langue) { this.langue = langue; }

    public Integer getRayonRechercheKm() { return rayonRechercheKm; }
    public void setRayonRechercheKm(Integer rayonRechercheKm) { this.rayonRechercheKm = rayonRechercheKm; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }

    public List<DemandePharmacien> getDemandes() { return demandes; }
    public void setDemandes(List<DemandePharmacien> demandes) { this.demandes = demandes; }
}
