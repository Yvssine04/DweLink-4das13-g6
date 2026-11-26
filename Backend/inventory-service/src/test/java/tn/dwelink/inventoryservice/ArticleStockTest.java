package tn.dwelink.inventoryservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import tn.dwelink.inventoryservice.document.*;
import tn.dwelink.inventoryservice.enums.*;

class ArticleStockTest {

    private Lot lot(int quantite, int reservee, OrigineLot origine, StatutLot statut, LocalDate peremption) {
        return Lot.builder().quantite(quantite).quantiteReservee(reservee)
                .origine(origine).statut(statut).datePeremption(peremption).build();
    }

    @Test
    void seulsLesLotsAchatActifsEtNonPerimesComptent() {
        LocalDate dansUnAn = LocalDate.now().plusYears(1);
        ArticleStock article = ArticleStock.builder().seuilMinimal(5).prixVente(BigDecimal.ONE).build();
        article.getLots().add(lot(10, 3, OrigineLot.ACHAT, StatutLot.ACTIF, dansUnAn));  // 7 libres
        article.getLots().add(lot(8, 0, OrigineLot.DON, StatutLot.ACTIF, dansUnAn));     // DON : jamais vendu
        article.getLots().add(lot(4, 0, OrigineLot.ACHAT, StatutLot.BLOQUE, dansUnAn));  // bloqué
        article.getLots().add(lot(6, 0, OrigineLot.ACHAT, StatutLot.ACTIF, LocalDate.now().minusDays(1))); // périmé

        assertThat(article.quantiteDisponible()).isEqualTo(7);
        assertThat(article.estFaible()).isFalse();
    }

    @Test
    void stockFaibleQuandOnAtteintLeSeuil() {
        ArticleStock article = ArticleStock.builder().seuilMinimal(5).build();
        article.getLots().add(lot(5, 0, OrigineLot.ACHAT, StatutLot.ACTIF, LocalDate.now().plusMonths(6)));
        assertThat(article.estFaible()).isTrue();   // 5 <= 5
    }
}