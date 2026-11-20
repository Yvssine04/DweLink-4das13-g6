package com.dwelink.pharmacy.repository;

import com.dwelink.pharmacy.entity.Garde;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GardeRepository extends JpaRepository<Garde, Long> {
    List<Garde> findByPharmacieId(Long pharmacieId);
    List<Garde> findByDebutLessThanEqualAndFinGreaterThanEqual(LocalDateTime now1, LocalDateTime now2);
}
