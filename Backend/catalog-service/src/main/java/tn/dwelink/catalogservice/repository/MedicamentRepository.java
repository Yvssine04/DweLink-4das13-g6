package tn.dwelink.catalogservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.dwelink.catalogservice.entity.Medicament;

public interface MedicamentRepository extends JpaRepository<Medicament, Long> {
    boolean existsByCodeBarres(String codeBarres);
}
