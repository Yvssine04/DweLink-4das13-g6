package com.dwelink.user.repository;

import com.dwelink.user.entity.DemandePharmacien;
import com.dwelink.user.entity.StatutDemande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DemandePharmacienRepository extends JpaRepository<DemandePharmacien, Long> {
    List<DemandePharmacien> findByStatut(StatutDemande statut);
    List<DemandePharmacien> findByUtilisateurId(Long utilisateurId);
}
