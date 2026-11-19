package tn.dwelink.pharmacyservice.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.dwelink.pharmacyservice.entity.MembrePharmacie;
import tn.dwelink.pharmacyservice.enums.RoleMembre;
import tn.dwelink.pharmacyservice.enums.StatutMembre;

public interface MembrePharmacieRepository extends JpaRepository<MembrePharmacie, Long> {

    Optional<MembrePharmacie> findByPharmacieIdAndUtilisateurId(Long pharmacieId, Long utilisateurId);

    boolean existsByPharmacieIdAndUtilisateurIdAndStatut(Long pharmacieId, Long utilisateurId, StatutMembre statut);

    boolean existsByPharmacieIdAndUtilisateurIdAndRoleAndStatut(Long pharmacieId, Long utilisateurId, RoleMembre role, StatutMembre statut);

    List<MembrePharmacie> findByUtilisateurId(Long utilisateurId);
}
