package tn.dwelink.inventoryservice.document;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import tn.dwelink.inventoryservice.enums.TypeMouvement;

@Document(collection = "mouvements_stock")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MouvementStock {

    @Id
    private String id;

    @Indexed                       // on cherchera l'historique d'un article par ce champ
    private String articleStockId; // lien vers l'article, comme une clé étrangère
    private String lotId;
    private String numeroLot;
    private TypeMouvement type;
    private Integer quantite;      // positif = entrée, négatif = sortie
    private String motif;
    private LocalDateTime dateMouvement;
}