package tn.dwelink.inventoryservice.dto;

import java.time.LocalDateTime;
import tn.dwelink.inventoryservice.document.MouvementStock;
import tn.dwelink.inventoryservice.enums.TypeMouvement;

public record MouvementResponse(
        String id, String articleStockId, String lotId, String numeroLot,
        TypeMouvement type, Integer quantite, String motif, LocalDateTime dateMouvement
) {
    public static MouvementResponse from(MouvementStock m) {
        return new MouvementResponse(m.getId(), m.getArticleStockId(), m.getLotId(), m.getNumeroLot(),
                m.getType(), m.getQuantite(), m.getMotif(), m.getDateMouvement());
    }
}