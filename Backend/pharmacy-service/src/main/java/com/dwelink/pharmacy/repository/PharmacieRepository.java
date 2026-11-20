package com.dwelink.pharmacy.repository;

import com.dwelink.pharmacy.entity.Pharmacie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PharmacieRepository extends JpaRepository<Pharmacie, Long> {
    List<Pharmacie> findByVilleIgnoreCase(String ville);
    boolean existsByNumeroAutorisation(String numeroAutorisation);
}
