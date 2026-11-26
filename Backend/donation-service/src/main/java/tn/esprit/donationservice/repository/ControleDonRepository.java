package tn.esprit.donationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.donationservice.entity.ControleDon;

public interface ControleDonRepository extends JpaRepository<ControleDon, Long> {
}
