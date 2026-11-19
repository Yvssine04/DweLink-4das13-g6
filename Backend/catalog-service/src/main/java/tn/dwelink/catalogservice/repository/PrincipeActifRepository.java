package tn.dwelink.catalogservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.dwelink.catalogservice.entity.PrincipeActif;

import java.util.List;

public interface PrincipeActifRepository extends JpaRepository<PrincipeActif, Long> {
    boolean existsByNomDciIgnoreCase(String nomDci);
    List<PrincipeActif> findByNomDciContainingIgnoreCase(String q);
}

