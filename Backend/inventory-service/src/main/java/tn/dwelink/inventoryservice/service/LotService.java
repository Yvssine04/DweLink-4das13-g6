package tn.dwelink.inventoryservice.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import tn.dwelink.inventoryservice.document.ArticleStock;
import tn.dwelink.inventoryservice.document.Lot;
import tn.dwelink.inventoryservice.dto.LotRequest;
import tn.dwelink.inventoryservice.dto.LotResponse;
import tn.dwelink.inventoryservice.enums.OrigineLot;
import tn.dwelink.inventoryservice.enums.StatutLot;
import tn.dwelink.inventoryservice.exception.ConflitException;
import tn.dwelink.inventoryservice.exception.RequeteInvalideException;
import tn.dwelink.inventoryservice.exception.RessourceIntrouvableException;
import tn.dwelink.inventoryservice.repository.ArticleStockRepository;


import java.time.LocalDateTime;
import tn.dwelink.inventoryservice.document.MouvementStock;
import tn.dwelink.inventoryservice.enums.TypeMouvement;
import tn.dwelink.inventoryservice.repository.MouvementStockRepository;

@Service
@RequiredArgsConstructor
public class LotService {

    private final ArticleStockService articleService;
    private final ArticleStockRepository articleRepository;
    private final MouvementStockRepository mouvementRepository;

    public LotResponse ajouter(String articleId, LotRequest req) {
        ArticleStock article = articleService.charger(articleId);
        if (!req.datePeremption().isAfter(LocalDate.now())) {
            throw new RequeteInvalideException("Le lot est déjà périmé");
        }
        boolean existe = article.getLots().stream().anyMatch(l -> l.getNumeroLot().equals(req.numeroLot()));
        if (existe) {
            throw new ConflitException("Le lot " + req.numeroLot() + " existe déjà");
        }
        Lot lot = Lot.builder()
                .id(new ObjectId().toHexString())   // un lot imbriqué n'a pas d'id automatique
                .numeroLot(req.numeroLot())
                .datePeremption(req.datePeremption())
                .quantite(req.quantite())
                .origine(req.origine() != null ? req.origine() : OrigineLot.ACHAT)
                .build();
        article.getLots().add(lot);
        articleRepository.save(article);// on enregistre l'article entier, lots compris
        mouvementRepository.save(MouvementStock.builder()
                .articleStockId(article.getId())
                .lotId(lot.getId())
                .numeroLot(lot.getNumeroLot())
                .type(TypeMouvement.ENTREE)
                .quantite(lot.getQuantite())
                .motif("Réception du lot")
                .dateMouvement(LocalDateTime.now())
                .build());
        return LotResponse.from(lot);
    }

    public LotResponse changerStatut(String lotId, StatutLot statut) {
        ArticleStock article = articleRepository.findByLotsId(lotId)
                .orElseThrow(() -> new RessourceIntrouvableException("Lot " + lotId + " introuvable"));
        Lot lot = article.getLots().stream()
                .filter(l -> l.getId().equals(lotId))
                .findFirst().orElseThrow();
        if (statut == StatutLot.ACTIF && lot.estPerime()) {
            throw new ConflitException("Un lot périmé ne peut pas être réactivé");
        }
        lot.setStatut(statut);
        articleRepository.save(article);
        return LotResponse.from(lot);
    }
}