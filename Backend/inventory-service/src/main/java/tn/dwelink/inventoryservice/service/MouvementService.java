package tn.dwelink.inventoryservice.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.dwelink.inventoryservice.document.ArticleStock;
import tn.dwelink.inventoryservice.document.Lot;
import tn.dwelink.inventoryservice.document.MouvementStock;
import tn.dwelink.inventoryservice.dto.MouvementRequest;
import tn.dwelink.inventoryservice.dto.MouvementResponse;
import tn.dwelink.inventoryservice.enums.OrigineLot;
import tn.dwelink.inventoryservice.enums.TypeMouvement;
import tn.dwelink.inventoryservice.exception.ConflitException;
import tn.dwelink.inventoryservice.exception.RequeteInvalideException;
import tn.dwelink.inventoryservice.repository.ArticleStockRepository;
import tn.dwelink.inventoryservice.repository.MouvementStockRepository;

import java.time.LocalDateTime;
import tn.dwelink.inventoryservice.exception.ConflitException;
import tn.dwelink.inventoryservice.exception.RessourceIntrouvableException;

@Service
@RequiredArgsConstructor
public class MouvementService {

    private final ArticleStockService articleService;
    private final ArticleStockRepository articleRepository;
    private final MouvementStockRepository mouvementRepository;

    // Point d'entrée : choisit l'action selon le type reçu
    public List<MouvementResponse> enregistrer(String articleId, MouvementRequest req) {
        return switch (req.type()) {
            case SORTIE -> vendre(articleId, req.quantite(), req.motif());
            case AJUSTEMENT -> ajuster(articleId, req.lotId(), req.quantite(), req.motif());
            default -> throw new RequeteInvalideException(
                    "Type " + req.type() + " interdit ici : ENTREE passe par l'ajout d'un lot");
        };
    }

    public List<MouvementResponse> vendre(String articleId, int quantite, String motif) {
        ArticleStock article = articleService.charger(articleId);
        if (quantite <= 0) {
            throw new RequeteInvalideException("La quantité d'une vente doit être positive");
        }
        if (article.quantiteDisponible() < quantite) {
            throw new ConflitException("Stock insuffisant : " + article.quantiteDisponible() + " boîte(s)");
        }

        // 1. Les lots vendables, du plus proche de la péremption au plus lointain
        List<Lot> lots = article.getLots().stream()
                .filter(l -> l.getOrigine() == OrigineLot.ACHAT && l.estUtilisable() && l.quantiteLibre() > 0)
                .sorted(Comparator.comparing(Lot::getDatePeremption))
                .toList();

        // 2. On prend dans chaque lot jusqu'à avoir la quantité demandée
        List<MouvementStock> mouvements = new ArrayList<>();
        int reste = quantite;
        for (Lot lot : lots) {
            if (reste == 0) break;
            int pris = Math.min(reste, lot.quantiteLibre());
            lot.setQuantite(lot.getQuantite() - pris);
            reste -= pris;
            mouvements.add(MouvementStock.builder()
                    .articleStockId(articleId).lotId(lot.getId()).numeroLot(lot.getNumeroLot())
                    .type(TypeMouvement.SORTIE).quantite(-pris).motif(motif)
                    .dateMouvement(LocalDateTime.now()).build());
        }

        // 3. Une seule écriture pour l'article et tous ses lots, puis l'historique
        articleRepository.save(article);
        return mouvementRepository.saveAll(mouvements).stream().map(MouvementResponse::from).toList();
    }

    // EXERCICE : à toi d'écrire le contenu (voir plus bas)
    public List<MouvementResponse> ajuster(String articleId, String lotId, int quantite, String motif) {
        if (lotId == null) {
            throw new RequeteInvalideException("lotId est obligatoire pour un ajustement");
        }
        if (quantite == 0) {
            throw new RequeteInvalideException("La quantité d'un ajustement ne peut pas être 0");
        }
        ArticleStock article = articleService.charger(articleId);

        Lot lot = article.getLots().stream()
                .filter(l -> l.getId().equals(lotId))
                .findFirst()
                .orElseThrow(() -> new RessourceIntrouvableException("Lot " + lotId + " introuvable dans cet article"));

        int nouvelleQuantite = lot.getQuantite() + quantite;
        if (nouvelleQuantite < lot.getQuantiteReservee()) {
            throw new ConflitException("Ajustement impossible : il resterait " + nouvelleQuantite
                    + " boîte(s) pour " + lot.getQuantiteReservee() + " réservée(s)");
        }
        lot.setQuantite(nouvelleQuantite);

        MouvementStock mouvement = MouvementStock.builder()
                .articleStockId(article.getId())
                .lotId(lot.getId())
                .numeroLot(lot.getNumeroLot())
                .type(TypeMouvement.AJUSTEMENT)
                .quantite(quantite)
                .motif(motif)
                .dateMouvement(LocalDateTime.now())
                .build();

        articleRepository.save(article);
        return List.of(MouvementResponse.from(mouvementRepository.save(mouvement)));
    }

    public List<MouvementResponse> historique(String articleId) {
        articleService.charger(articleId); // 404 si l'article n'existe pas
        return mouvementRepository.findByArticleStockIdOrderByDateMouvementDesc(articleId).stream()
                .map(MouvementResponse::from).toList();
    }
}