package com.dwelink.reservation.service;

import com.dwelink.reservation.dto.request.CreerReservationRequest;
import com.dwelink.reservation.dto.request.LigneReservationRequest;
import com.dwelink.reservation.dto.response.HistoriqueStatutResponse;
import com.dwelink.reservation.dto.response.LigneReservationResponse;
import com.dwelink.reservation.dto.response.ReservationResponse;
import com.dwelink.reservation.entity.HistoriqueStatutReservation;
import com.dwelink.reservation.entity.LigneReservation;
import com.dwelink.reservation.entity.Reservation;
import com.dwelink.reservation.enums.StatutReservation;
import com.dwelink.reservation.exception.AccesInterditException;
import com.dwelink.reservation.exception.ConflitException;
import com.dwelink.reservation.exception.ReservationIntrouvableException;
import com.dwelink.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private static final int MAX_RESERVATIONS_ACTIVES = 3;
    private static final List<StatutReservation> STATUTS_ACTIFS = List.of(
            StatutReservation.EN_ATTENTE,
            StatutReservation.CONFIRMEE
    );

    private final ReservationRepository reservationRepository;

    @Transactional
    public ReservationResponse creer(Long patientId, CreerReservationRequest request) {
        long actives = reservationRepository.countByPatientIdAndStatutIn(patientId, STATUTS_ACTIFS);
        if (actives >= MAX_RESERVATIONS_ACTIVES) {
            throw new ConflitException("Un patient ne peut pas avoir plus de 3 réservations actives");
        }

        Reservation reservation = new Reservation();
        reservation.setReference(genererReference());
        reservation.setPatientId(patientId);
        reservation.setPharmacieId(request.pharmacieId());
        for (LigneReservationRequest ligneRequest : request.lignes()) {
            reservation.getLignes().add(toLigne(ligneRequest, reservation));
        }
        reservation.enregistrerCreation(patientId);
        return toResponse(reservationRepository.save(reservation));
    }

    @Transactional(readOnly = true)
    public ReservationResponse trouverPourPatient(Long id, Long patientId) {
        Reservation reservation = trouver(id);
        verifierProprietaire(reservation, patientId);
        return toResponse(reservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> mesReservations(Long patientId, StatutReservation statut) {
        List<Reservation> reservations = statut == null
                ? reservationRepository.findByPatientIdOrderByDateCreationDesc(patientId)
                : reservationRepository.findByPatientIdAndStatutOrderByDateCreationDesc(patientId, statut);
        return reservations.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> reservationsPharmacie(Long pharmacienId, Long pharmacieId, StatutReservation statut) {
        if (pharmacienId == null) {
            throw new AccesInterditException("Vous n'êtes pas autorisé à consulter ces réservations");
        }
        List<Reservation> reservations = statut == null
                ? reservationRepository.findByPharmacieIdOrderByDateCreationDesc(pharmacieId)
                : reservationRepository.findByPharmacieIdAndStatutOrderByDateCreationDesc(pharmacieId, statut);
        return reservations.stream().map(this::toResponse).toList();
    }

    @Transactional
    public ReservationResponse confirmer(Long id, Long pharmacienId) {
        Reservation reservation = trouver(id);
        reservation.confirmer(pharmacienId);
        return toResponse(reservation);
    }

    @Transactional
    public ReservationResponse refuser(Long id, Long pharmacienId, String motif) {
        Reservation reservation = trouver(id);
        reservation.refuser(pharmacienId, motif);
        return toResponse(reservation);
    }

    @Transactional
    public ReservationResponse annuler(Long id, Long patientId) {
        Reservation reservation = trouver(id);
        verifierProprietaire(reservation, patientId);
        reservation.annuler(patientId);
        return toResponse(reservation);
    }

    @Transactional
    public ReservationResponse terminer(Long id, Long pharmacienId, String codeRetrait) {
        Reservation reservation = trouver(id);
        reservation.terminer(codeRetrait);
        HistoriqueStatutReservation dernier = reservation.getHistorique().get(reservation.getHistorique().size() - 1);
        dernier.setActeurId(pharmacienId);
        return toResponse(reservation);
    }

    @Transactional(readOnly = true)
    public List<HistoriqueStatutResponse> historique(Long id, Long patientId) {
        Reservation reservation = trouver(id);
        verifierProprietaire(reservation, patientId);
        return reservation.getHistorique().stream().map(HistoriqueStatutResponse::from).toList();
    }

    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void expirerEcheances() {
        LocalDateTime maintenant = LocalDateTime.now();
        List<Reservation> enAttente = reservationRepository
                .findByStatutAndDateLimiteReponseLessThanEqual(StatutReservation.EN_ATTENTE, maintenant);
        List<Reservation> confirmees = reservationRepository
                .findByStatutAndDateLimiteRetraitLessThanEqual(StatutReservation.CONFIRMEE, maintenant);
        int expirees = 0;
        for (Reservation reservation : enAttente) {
            reservation.expirer();
            expirees++;
        }
        for (Reservation reservation : confirmees) {
            reservation.expirer();
            expirees++;
        }
        if (expirees > 0) {
            log.info("{} réservation(s) expirée(s)", expirees);
        }
    }

    private Reservation trouver(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationIntrouvableException("Réservation introuvable"));
    }

    private void verifierProprietaire(Reservation reservation, Long patientId) {
        if (!reservation.getPatientId().equals(patientId)) {
            throw new AccesInterditException("Vous n'êtes pas autorisé à accéder à cette réservation");
        }
    }

    private String genererReference() {
        String reference;
        do {
            reference = "RSV-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        } while (reservationRepository.existsByReference(reference));
        return reference;
    }

    private LigneReservation toLigne(LigneReservationRequest request, Reservation reservation) {
        LigneReservation ligne = new LigneReservation();
        ligne.setMedicamentId(request.medicamentId());
        ligne.setNomMedicament(request.nomMedicament().trim());
        ligne.setQuantite(request.quantite());
        ligne.setPrixUnitaire(request.prixUnitaire());
        ligne.setOrdonnanceRequise(request.ordonnanceRequise());
        ligne.setReservation(reservation);
        return ligne;
    }

    private ReservationResponse toResponse(Reservation reservation) {
        List<LigneReservationResponse> lignes = reservation.getLignes().stream()
                .map(LigneReservationResponse::from)
                .toList();
        return ReservationResponse.from(reservation, lignes);
    }
}
