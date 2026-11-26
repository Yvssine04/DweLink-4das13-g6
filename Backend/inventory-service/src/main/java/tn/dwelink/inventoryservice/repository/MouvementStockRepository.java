package tn.dwelink.inventoryservice.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import tn.dwelink.inventoryservice.document.MouvementStock;

public interface MouvementStockRepository extends MongoRepository<MouvementStock, String> {

    List<MouvementStock> findByArticleStockIdOrderByDateMouvementDesc(String articleStockId);
}