package tn.dwelink.inventoryservice.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import tn.dwelink.inventoryservice.document.ArticleStock;

public interface ArticleStockRepository extends MongoRepository<ArticleStock, String> {

    List<ArticleStock> findByPharmacieId(Long pharmacieId);

    boolean existsByPharmacieIdAndMedicamentId(Long pharmacieId, Long medicamentId);

    // Cherche l'article qui contient le lot : MongoDB regarde dans la liste "lots"
    Optional<ArticleStock> findByLotsId(String lotId);
}