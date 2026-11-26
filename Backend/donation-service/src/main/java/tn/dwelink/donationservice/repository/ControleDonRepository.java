package tn.dwelink.donationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.dwelink.donationservice.entity.ControleDon;

public interface ControleDonRepository extends JpaRepository<ControleDon, Long> {
}
