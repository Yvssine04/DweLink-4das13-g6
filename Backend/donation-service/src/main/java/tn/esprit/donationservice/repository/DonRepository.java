package tn.esprit.donationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tn.esprit.donationservice.entity.Don;
import tn.esprit.donationservice.enums.StatutDon;

import java.util.List;

public interface DonRepository extends JpaRepository<Don, Long> {
    List<Don> findByDonateurId(Long donateurId);
    Page<Don> findByPharmacieId(Long pharmacieId, Pageable pageable);
    Page<Don> findByStatut(StatutDon statut, Pageable pageable);
    Page<Don> findByPharmacieIdAndStatut(Long pharmacieId, StatutDon statut, Pageable pageable);
}
