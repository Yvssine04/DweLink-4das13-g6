package tn.dwelink.pharmacyservice.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.dwelink.pharmacyservice.entity.Garde;

public interface GardeRepository extends JpaRepository<Garde, Long> {

    List<Garde> findByPharmacieId(Long pharmacieId);

    boolean existsByPharmacieIdAndDebutLessThanAndFinGreaterThan(Long pharmacieId, LocalDateTime fin, LocalDateTime debut);

    List<Garde> findByDebutLessThanEqualAndFinGreaterThanEqual(LocalDateTime maintenant1, LocalDateTime maintenant2);
}
