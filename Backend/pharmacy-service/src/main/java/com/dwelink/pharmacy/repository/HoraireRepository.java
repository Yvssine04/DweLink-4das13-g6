package com.dwelink.pharmacy.repository;

import com.dwelink.pharmacy.entity.Horaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoraireRepository extends JpaRepository<Horaire, Long> {
    List<Horaire> findByPharmacieId(Long pharmacieId);
    void deleteByPharmacieId(Long pharmacieId);
}
