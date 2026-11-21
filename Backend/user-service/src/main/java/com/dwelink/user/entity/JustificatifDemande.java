package com.dwelink.user.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "justificatif_demande")
public class JustificatifDemande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demande_id", nullable = false)
    private DemandePharmacien demande;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TypeJustificatif type;

    @Column(name = "nom_fichier", nullable = false)
    private String nomFichier;

    @Column(name = "date_ajout", updatable = false)
    private LocalDateTime dateAjout;

    @PrePersist
    protected void onCreate() {
        this.dateAjout = LocalDateTime.now();
    }

    public JustificatifDemande() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public DemandePharmacien getDemande() { return demande; }
    public void setDemande(DemandePharmacien demande) { this.demande = demande; }

    public TypeJustificatif getType() { return type; }
    public void setType(TypeJustificatif type) { this.type = type; }

    public String getNomFichier() { return nomFichier; }
    public void setNomFichier(String nomFichier) { this.nomFichier = nomFichier; }

    public LocalDateTime getDateAjout() { return dateAjout; }
    public void setDateAjout(LocalDateTime dateAjout) { this.dateAjout = dateAjout; }
}
