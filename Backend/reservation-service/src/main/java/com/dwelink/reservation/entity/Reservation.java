package com.dwelink.reservation.entity;

import com.dwelink.reservation.enums.StatutReservation;
import com.dwelink.reservation.enums.TypeActeur;
import com.dwelink.reservation.exception.ConflitException;
import com.dwelink.reservation.exception.RequeteInvalideException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reservation", indexes = {
        @Index(name = "idx_reservation_patient_statut", columnList = "patient_id, statut"),
        @Index(name = "idx_reservation_pharmacie_statut", columnList = "pharmacie_id, statut")
})
@Getter
@Setter
@NoArgsConstructor
public class Reservation {

    private static final String CODE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 32)
    private String reference;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Column(name = "pharmacie_id", nullable = false)
    private Long pharmacieId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutReservation statut;

    @Column(length = 6)
    private String codeRetrait;

    @Column(nullable = false)
    private LocalDateTime dateCreation;

    @Column(nullable = false)
    private LocalDateTime dateLimiteReponse;

    private LocalDateTime dateLimiteRetrait;

    @Version
    private Integer version;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<LigneReservation> lignes = new ArrayList<>();

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dateChangement ASC")
    private List<HistoriqueStatutReservation> historique = new ArrayList<>();

    public void enregistrerCreation(Long patientId) {
        this.statut = StatutReservation.EN_ATTENTE;
        this.dateCreation = LocalDateTime.now();
        this.dateLimiteReponse = this.dateCreation.plusHours(2);
        ajouterHistorique(null, StatutReservation.EN_ATTENTE, patientId, TypeActeur.PATIENT,
                "Création de la réservation");
    }

    public void confirmer(Long pharmacienId) {
        if (this.statut != StatutReservation.EN_ATTENTE) {
            throw new ConflitException("Seule une réservation en attente peut être confirmée");
        }
        if (!LocalDateTime.now().isBefore(this.dateLimiteReponse)) {
            throw new ConflitException("Le délai de réponse de la pharmacie est dépassé");
        }
        this.codeRetrait = genererCodeRetrait();
        this.dateLimiteRetrait = LocalDateTime.now().plusHours(24);
        changerStatut(StatutReservation.CONFIRMEE, pharmacienId, TypeActeur.PHARMACIEN,
                "Confirmation de la réservation");
    }

    public void refuser(Long pharmacienId, String motif) {
        if (motif == null || motif.isBlank()) {
            throw new RequeteInvalideException("Le motif est obligatoire");
        }
        if (this.statut != StatutReservation.EN_ATTENTE) {
            throw new ConflitException("Seule une réservation en attente peut être refusée");
        }
        if (!LocalDateTime.now().isBefore(this.dateLimiteReponse)) {
            throw new ConflitException("Le délai de réponse de la pharmacie est dépassé");
        }
        changerStatut(StatutReservation.REFUSEE, pharmacienId, TypeActeur.PHARMACIEN, motif.trim());
    }

    public void annuler(Long patientId) {
        if (this.statut == StatutReservation.TERMINEE
                || this.statut == StatutReservation.REFUSEE
                || this.statut == StatutReservation.ANNULEE
                || this.statut == StatutReservation.EXPIREE) {
            throw new ConflitException("Cette réservation ne peut plus être annulée");
        }
        changerStatut(StatutReservation.ANNULEE, patientId, TypeActeur.PATIENT, "Annulation par le patient");
    }

    public void terminer(String codeRetrait) {
        if (this.statut != StatutReservation.CONFIRMEE) {
            throw new ConflitException("Seule une réservation confirmée peut être terminée");
        }
        if (this.dateLimiteRetrait != null && !LocalDateTime.now().isBefore(this.dateLimiteRetrait)) {
            throw new ConflitException("Le délai de retrait est dépassé");
        }
        if (codeRetrait == null || codeRetrait.isBlank() || !codeRetrait.equals(this.codeRetrait)) {
            throw new RequeteInvalideException("Code de retrait incorrect");
        }
        changerStatut(StatutReservation.TERMINEE, null, TypeActeur.PHARMACIEN, "Retrait validé");
    }

    public void expirer() {
        LocalDateTime maintenant = LocalDateTime.now();
        if (this.statut == StatutReservation.EN_ATTENTE
                && this.dateLimiteReponse != null
                && !this.dateLimiteReponse.isAfter(maintenant)) {
            changerStatut(StatutReservation.EXPIREE, null, TypeActeur.SYSTEME,
                    "Délai de réponse de la pharmacie dépassé");
            return;
        }
        if (this.statut == StatutReservation.CONFIRMEE
                && this.dateLimiteRetrait != null
                && !this.dateLimiteRetrait.isAfter(maintenant)) {
            changerStatut(StatutReservation.EXPIREE, null, TypeActeur.SYSTEME, "Délai de retrait dépassé");
            return;
        }
        throw new ConflitException("La réservation ne peut pas expirer dans son état actuel");
    }

    public Boolean estActive() {
        return this.statut == StatutReservation.EN_ATTENTE || this.statut == StatutReservation.CONFIRMEE;
    }

    private void changerStatut(StatutReservation nouveau, Long acteurId, TypeActeur typeActeur, String motif) {
        StatutReservation ancien = this.statut;
        this.statut = nouveau;
        ajouterHistorique(ancien, nouveau, acteurId, typeActeur, motif);
    }

    private void ajouterHistorique(StatutReservation ancien, StatutReservation nouveau, Long acteurId,
                                   TypeActeur typeActeur, String motif) {
        HistoriqueStatutReservation entree = new HistoriqueStatutReservation();
        entree.setAncienStatut(ancien);
        entree.setNouveauStatut(nouveau);
        entree.setActeurId(acteurId);
        entree.setTypeActeur(typeActeur);
        entree.setMotif(motif);
        entree.setDateChangement(LocalDateTime.now());
        entree.setReservation(this);
        this.historique.add(entree);
    }

    private static String genererCodeRetrait() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CODE_ALPHABET.charAt(RANDOM.nextInt(CODE_ALPHABET.length())));
        }
        return code.toString();
    }
}
