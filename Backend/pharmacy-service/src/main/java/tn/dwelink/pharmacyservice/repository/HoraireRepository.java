package tn.dwelink.pharmacyservice.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.dwelink.pharmacyservice.entity.Horaire;

public interface HoraireRepository extends JpaRepository<Horaire, Long> {

    List<Horaire> findByPharmacieId(Long pharmacieId);
}
