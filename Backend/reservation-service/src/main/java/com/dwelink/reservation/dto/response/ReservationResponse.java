package com.dwelink.reservation.dto.response;

import com.dwelink.reservation.entity.Reservation;
import com.dwelink.reservation.enums.StatutReservation;

import java.time.LocalDateTime;
import java.util.List;

public record ReservationResponse(
        Long id,
        String reference,
        Long patientId,
        Long pharmacieId,
        StatutReservation statut,
        String codeRetrait,
        LocalDateTime dateCreation,
        LocalDateTime dateLimiteReponse,
        LocalDateTime dateLimiteRetrait,
        List<LigneReservationResponse> lignes
) {

    public static ReservationResponse from(Reservation reservation, List<LigneReservationResponse> lignes) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getReference(),
                reservation.getPatientId(),
                reservation.getPharmacieId(),
                reservation.getStatut(),
                reservation.getCodeRetrait(),
                reservation.getDateCreation(),
                reservation.getDateLimiteReponse(),
                reservation.getDateLimiteRetrait(),
                lignes
        );
    }
}
