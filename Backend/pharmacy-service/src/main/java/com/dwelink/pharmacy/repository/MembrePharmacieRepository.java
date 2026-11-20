package com.dwelink.pharmacy.repository;

import com.dwelink.pharmacy.entity.MembrePharmacie;
import com.dwelink.pharmacy.entity.StatutMembre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembrePharmacieRepository extends JpaRepository<MembrePharmacie, Long> {
    List<MembrePharmacie> findByPharmacieId(Long pharmacieId);
    Optional<MembrePharmacie> findByPharmacieIdAndUtilisateurId(Long pharmacieId, Long utilisateurId);
    boolean existsByPharmacieIdAndUtilisateurIdAndStatut(Long pharmacieId, Long utilisateurId, StatutMembre statut);
}
