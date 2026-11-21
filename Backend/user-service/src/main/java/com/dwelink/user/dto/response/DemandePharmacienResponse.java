package com.dwelink.user.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class DemandePharmacienResponse {
    private Long id;
    private Long utilisateurId;
    private String emailUtilisateur;
    private String nomCompletUtilisateur;
    private String numeroOrdre;
    private String diplome;
    private String statut;
    private LocalDateTime dateDepot;
    private LocalDateTime dateDecision;
    private Long examineePar;
    private String commentaireDecision;
    private List<JustificatifResponse> justificatifs;

    public DemandePharmacienResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(Long utilisateurId) { this.utilisateurId = utilisateurId; }

    public String getEmailUtilisateur() { return emailUtilisateur; }
    public void setEmailUtilisateur(String emailUtilisateur) { this.emailUtilisateur = emailUtilisateur; }

    public String getNomCompletUtilisateur() { return nomCompletUtilisateur; }
    public void setNomCompletUtilisateur(String nomCompletUtilisateur) { this.nomCompletUtilisateur = nomCompletUtilisateur; }

    public String getNumeroOrdre() { return numeroOrdre; }
    public void setNumeroOrdre(String numeroOrdre) { this.numeroOrdre = numeroOrdre; }

    public String getDiplome() { return diplome; }
    public void setDiplome(String diplome) { this.diplome = diplome; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public LocalDateTime getDateDepot() { return dateDepot; }
    public void setDateDepot(LocalDateTime dateDepot) { this.dateDepot = dateDepot; }

    public LocalDateTime getDateDecision() { return dateDecision; }
    public void setDateDecision(LocalDateTime dateDecision) { this.dateDecision = dateDecision; }

    public Long getExamineePar() { return examineePar; }
    public void setExamineePar(Long examineePar) { this.examineePar = examineePar; }

    public String getCommentaireDecision() { return commentaireDecision; }
    public void setCommentaireDecision(String commentaireDecision) { this.commentaireDecision = commentaireDecision; }

    public List<JustificatifResponse> getJustificatifs() { return justificatifs; }
    public void setJustificatifs(List<JustificatifResponse> justificatifs) { this.justificatifs = justificatifs; }
}
