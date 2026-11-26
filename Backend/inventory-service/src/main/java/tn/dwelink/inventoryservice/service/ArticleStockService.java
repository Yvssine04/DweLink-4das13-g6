package tn.dwelink.inventoryservice.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.dwelink.inventoryservice.document.ArticleStock;
import tn.dwelink.inventoryservice.dto.*;
import tn.dwelink.inventoryservice.exception.*;
import tn.dwelink.inventoryservice.repository.ArticleStockRepository;

@Service
@RequiredArgsConstructor
public class ArticleStockService {

    private final ArticleStockRepository articleRepository;

    // C : créer
    public ArticleStockResponse creer(ArticleStockRequest req) {
        if (articleRepository.existsByPharmacieIdAndMedicamentId(req.pharmacieId(), req.medicamentId())) {
            throw new ConflitException("Ce médicament est déjà dans le stock de cette pharmacie");
        }
        ArticleStock article = ArticleStock.builder()
                .pharmacieId(req.pharmacieId())
                .medicamentId(req.medicamentId())
                .seuilMinimal(req.seuilMinimal())
                .prixVente(req.prixVente())
                .build();
        return ArticleStockResponse.from(articleRepository.save(article));
    }

    // R : lire la liste d'une pharmacie
    public List<ArticleStockResponse> lister(Long pharmacieId) {
        return articleRepository.findByPharmacieId(pharmacieId).stream()
                .map(ArticleStockResponse::from)
                .toList();
    }

    // R : lire un seul article
    public ArticleStockResponse trouver(String id) {
        return ArticleStockResponse.from(charger(id));
    }

    // U : modifier le prix et/ou le seuil
    public ArticleStockResponse modifier(String id, ArticleStockUpdateRequest req) {
        if (req.seuilMinimal() == null && req.prixVente() == null) {
            throw new RequeteInvalideException("Rien à modifier : envoyez seuilMinimal et/ou prixVente");
        }
        ArticleStock article = charger(id);
        if (req.seuilMinimal() != null) article.setSeuilMinimal(req.seuilMinimal());
        if (req.prixVente() != null) article.setPrixVente(req.prixVente());
        return ArticleStockResponse.from(articleRepository.save(article));
    }

    // D : supprimer (refusé si des boîtes sont réservées)
    public void supprimer(String id) {
        ArticleStock article = charger(id);
        boolean reservees = article.getLots().stream().anyMatch(l -> l.getQuantiteReservee() > 0);
        if (reservees) {
            throw new ConflitException("Impossible de supprimer : des boîtes sont réservées");
        }
        articleRepository.delete(article);
    }

    // Utilisée partout : charge l'article ou lève 404
    public ArticleStock charger(String id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Article " + id + " introuvable"));
    }
}