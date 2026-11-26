package tn.dwelink.inventoryservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ArticleStockRequest(
        @NotNull(message = "La pharmacie est obligatoire") Long pharmacieId,
        @NotNull(message = "Le médicament est obligatoire") Long medicamentId,
        @NotNull @Min(value = 0, message = "Le seuil doit être positif") Integer seuilMinimal,
        @NotNull @DecimalMin(value = "0.0", message = "Le prix doit être positif") BigDecimal prixVente
) {
}