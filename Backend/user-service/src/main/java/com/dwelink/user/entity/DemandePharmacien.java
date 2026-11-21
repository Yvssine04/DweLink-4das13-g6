package com.dwelink.user.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "demande_pharmacien")
public class DemandePharmacien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private ProfilUtilisateur utilisateur;

    @Column(name = "numero_ordre", nullable = false, length = 100)
    private String numeroOrdre;

    @Column(nullable = false)
    private String diplome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StatutDemande statut;

    @Column(name = "date_depot", updatable = false)
    private LocalDateTime dateDepot;

    @Column(name = "date_decision")
    private LocalDateTime dateDecision;

    @Column(name = "examinee_par")
    private Long examineePar;

    @Column(name = "commentaire_decision", columnDefinition = "TEXT")
    private String commentaireDecision;

    @OneToMany(mappedBy = "demande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JustificatifDemande> justificatifs = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.dateDepot = LocalDateTime.now();
        if (this.statut == null) {
            this.statut = StatutDemande.DEPOSEE;
        }
    }

    public DemandePharmacien() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ProfilUtilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(ProfilUtilisateur utilisateur) { this.utilisateur = utilisateur; }

    public String getNumeroOrdre() { return numeroOrdre; }
    public void setNumeroOrdre(String numeroOrdre) { this.numeroOrdre = numeroOrdre; }

    public String getDiplome() { return diplome; }
    public void setDiplome(String diplome) { this.diplome = diplome; }

    public StatutDemande getStatut() { return statut; }
    public void setStatut(StatutDemande statut) { this.statut = statut; }

    public LocalDateTime getDateDepot() { return dateDepot; }
    public void setDateDepot(LocalDateTime dateDepot) { this.dateDepot = dateDepot; }

    public LocalDateTime getDateDecision() { return dateDecision; }
    public void setDateDecision(LocalDateTime dateDecision) { this.dateDecision = dateDecision; }

    public Long getExamineePar() { return examineePar; }
    public void setExamineePar(Long examineePar) { this.examineePar = examineePar; }

    public String getCommentaireDecision() { return commentaireDecision; }
    public void setCommentaireDecision(String commentaireDecision) { this.commentaireDecision = commentaireDecision; }

    public List<JustificatifDemande> getJustificatifs() { return justificatifs; }
    public void setJustificatifs(List<JustificatifDemande> justificatifs) { this.justificatifs = justificatifs; }
}
