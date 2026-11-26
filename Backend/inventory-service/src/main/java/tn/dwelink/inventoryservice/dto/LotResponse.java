package tn.dwelink.inventoryservice.dto;

import java.time.LocalDate;
import tn.dwelink.inventoryservice.document.Lot;
import tn.dwelink.inventoryservice.enums.OrigineLot;
import tn.dwelink.inventoryservice.enums.StatutLot;

public record LotResponse(
        String id, String numeroLot, LocalDate datePeremption, Integer quantite,
        Integer quantiteReservee, OrigineLot origine, StatutLot statut, boolean estPerime
) {
    public static LotResponse from(Lot l) {
        return new LotResponse(l.getId(), l.getNumeroLot(), l.getDatePeremption(), l.getQuantite(),
                l.getQuantiteReservee(), l.getOrigine(), l.getStatut(), l.estPerime());
    }
}