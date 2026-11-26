package com.dwelink.reservation.repository;

import com.dwelink.reservation.entity.Reservation;
import com.dwelink.reservation.enums.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    long countByPatientIdAndStatutIn(Long patientId, Collection<StatutReservation> statuts);

    boolean existsByReference(String reference);

    List<Reservation> findByPatientIdOrderByDateCreationDesc(Long patientId);

    List<Reservation> findByPatientIdAndStatutOrderByDateCreationDesc(Long patientId, StatutReservation statut);

    List<Reservation> findByPharmacieIdOrderByDateCreationDesc(Long pharmacieId);

    List<Reservation> findByPharmacieIdAndStatutOrderByDateCreationDesc(Long pharmacieId, StatutReservation statut);

    List<Reservation> findByStatutAndDateLimiteReponseLessThanEqual(StatutReservation statut, LocalDateTime date);

    List<Reservation> findByStatutAndDateLimiteRetraitLessThanEqual(StatutReservation statut, LocalDateTime date);
}
