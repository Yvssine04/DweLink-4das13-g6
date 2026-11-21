package com.dwelink.user.repository;

import com.dwelink.user.entity.JustificatifDemande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JustificatifDemandeRepository extends JpaRepository<JustificatifDemande, Long> {
    List<JustificatifDemande> findByDemandeId(Long demandeId);
}
