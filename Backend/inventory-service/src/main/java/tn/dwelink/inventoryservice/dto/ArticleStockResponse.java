package tn.dwelink.inventoryservice.dto;

import java.math.BigDecimal;
import java.util.List;
import tn.dwelink.inventoryservice.document.ArticleStock;

public record ArticleStockResponse(
        String id, Long pharmacieId, Long medicamentId,
        Integer seuilMinimal, BigDecimal prixVente,
        int quantiteDisponible, boolean estFaible,
        List<LotResponse> lots                                        // nouveau
) {
    public static ArticleStockResponse from(ArticleStock a) {
        return new ArticleStockResponse(a.getId(), a.getPharmacieId(), a.getMedicamentId(),
                a.getSeuilMinimal(), a.getPrixVente(), a.quantiteDisponible(), a.estFaible(),
                a.getLots().stream().map(LotResponse::from).toList()); // nouveau
    }
}