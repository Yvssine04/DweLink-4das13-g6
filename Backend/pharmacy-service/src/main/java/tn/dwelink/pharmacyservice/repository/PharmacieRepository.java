package tn.dwelink.pharmacyservice.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.dwelink.pharmacyservice.entity.Pharmacie;
import tn.dwelink.pharmacyservice.enums.StatutPharmacie;

public interface PharmacieRepository extends JpaRepository<Pharmacie, Long> {

    boolean existsByNumeroAutorisation(String numeroAutorisation);

    List<Pharmacie> findByStatut(StatutPharmacie statut);

    List<Pharmacie> findByVilleIgnoreCaseAndStatut(String ville, StatutPharmacie statut);
}
