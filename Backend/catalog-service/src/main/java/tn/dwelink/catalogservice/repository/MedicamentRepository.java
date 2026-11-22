package tn.dwelink.catalogservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.dwelink.catalogservice.entity.Medicament;
import tn.dwelink.catalogservice.enums.FormePharmaceutique;
import tn.dwelink.catalogservice.enums.StatutMedicament;

import java.util.List;

public interface MedicamentRepository extends JpaRepository<Medicament, Long> {
    boolean existsByCodeBarres(String codeBarres);
    boolean existsByCodeBarresAndIdNot(String codeBarres, Long id);
    List<Medicament> findByFormeAndStatutAndIdNot(FormePharmaceutique forme,
                                                  StatutMedicament statut, Long id);
}
