package tn.dwelink.inventoryservice.document;

import java.time.LocalDate;
import lombok.*;
import tn.dwelink.inventoryservice.enums.OrigineLot;
import tn.dwelink.inventoryservice.enums.StatutLot;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Lot {

    private String id;              // on le génère nous-mêmes (partie K)
    private String numeroLot;
    private LocalDate datePeremption;
    private Integer quantite;
    @Builder.Default
    private Integer quantiteReservee = 0;
    private OrigineLot origine;
    @Builder.Default
    private StatutLot statut = StatutLot.ACTIF;

    public boolean estPerime() {
        return datePeremption.isBefore(LocalDate.now());
    }

    // Un lot bloqué ou périmé n'est ni vendu ni réservé (règle du rapport)
    public boolean estUtilisable() {
        return statut == StatutLot.ACTIF && !estPerime();
    }

    public int quantiteLibre() {
        return quantite - quantiteReservee;
    }
}