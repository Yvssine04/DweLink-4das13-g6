package com.dwelink.reservation.dto.response;

import com.dwelink.reservation.entity.LigneReservation;

import java.math.BigDecimal;

public record LigneReservationResponse(
        Long id,
        Long medicamentId,
        String nomMedicament,
        Integer quantite,
        BigDecimal prixUnitaire,
        Boolean ordonnanceRequise
) {

    public static LigneReservationResponse from(LigneReservation ligne) {
        return new LigneReservationResponse(
                ligne.getId(),
                ligne.getMedicamentId(),
                ligne.getNomMedicament(),
                ligne.getQuantite(),
                ligne.getPrixUnitaire(),
                ligne.getOrdonnanceRequise()
        );
    }
}
