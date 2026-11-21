package com.dwelink.user.repository;

import com.dwelink.user.entity.ProfilUtilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfilUtilisateurRepository extends JpaRepository<ProfilUtilisateur, Long> {
    Optional<ProfilUtilisateur> findByEmail(String email);
    boolean existsByEmail(String email);
}
