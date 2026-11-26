package tn.dwelink.inventoryservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

public record ArticleStockUpdateRequest(
        @Min(0) Integer seuilMinimal,
        @DecimalMin("0.0") BigDecimal prixVente
) {
}