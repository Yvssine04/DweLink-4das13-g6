package com.dwelink.reservation.dto.response;

import com.dwelink.reservation.entity.HistoriqueStatutReservation;
import com.dwelink.reservation.enums.StatutReservation;
import com.dwelink.reservation.enums.TypeActeur;

import java.time.LocalDateTime;

public record HistoriqueStatutResponse(
        Long id,
        StatutReservation ancienStatut,
        StatutReservation nouveauStatut,
        Long acteurId,
        TypeActeur typeActeur,
        String motif,
        LocalDateTime dateChangement
) {

    public static HistoriqueStatutResponse from(HistoriqueStatutReservation historique) {
        return new HistoriqueStatutResponse(
                historique.getId(),
                historique.getAncienStatut(),
                historique.getNouveauStatut(),
                historique.getActeurId(),
                historique.getTypeActeur(),
                historique.getMotif(),
                historique.getDateChangement()
        );
    }
}
