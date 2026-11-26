package tn.dwelink.inventoryservice.document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import tn.dwelink.inventoryservice.enums.OrigineLot;

@Document(collection = "articles_stock")
@CompoundIndex(name = "pharmacie_medicament_unique",
        def = "{'pharmacieId': 1, 'medicamentId': 1}", unique = true)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ArticleStock {

    @Id
    private String id;
    private Long pharmacieId;     // id de pharmacy-service (PostgreSQL), donc Long
    private Long medicamentId;    // id de catalog-service (PostgreSQL), donc Long
    private Integer seuilMinimal;
    private BigDecimal prixVente;

    @Builder.Default
    private List<Lot> lots = new ArrayList<>();

    @Version
    private Long version;

    // Boîtes vendables : lots ACHAT utilisables, moins les boîtes réservées
    public int quantiteDisponible() {
        return lots.stream()
                .filter(l -> l.getOrigine() == OrigineLot.ACHAT && l.estUtilisable())
                .mapToInt(Lot::quantiteLibre)
                .sum();
    }

    public boolean estFaible() {
        return quantiteDisponible() <= seuilMinimal;
    }
}