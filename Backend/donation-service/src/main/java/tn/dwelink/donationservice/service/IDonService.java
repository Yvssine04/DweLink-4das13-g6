package tn.dwelink.donationservice.service;

import org.springframework.data.domain.Page;
import tn.dwelink.donationservice.entity.Don;
import tn.dwelink.donationservice.enums.StatutDon;

import java.util.List;

public interface IDonService {
    Don addDon(Don don);
    Don updateDon(Long id, Don don);
    void deleteDon(Long id);
    Don retrieveDon(Long id);
    List<Don> retrieveAllDons();
    List<Don> retrieveMesDons(Long donateurId);
    Page<Don> retrieveDons(Long pharmacieId, StatutDon statut, int page);
    Don annulerDon(Long id);
    Don prendreDecision(Long id, StatutDon statut);
}
