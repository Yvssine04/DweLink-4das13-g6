package tn.dwelink.donationservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tn.dwelink.donationservice.entity.ControleDon;
import tn.dwelink.donationservice.entity.Don;
import tn.dwelink.donationservice.entity.RemiseSolidaire;
import tn.dwelink.donationservice.enums.StatutDon;
import tn.dwelink.donationservice.repository.DonRepository;

import java.util.List;

@Service
public class DonServiceImpl implements IDonService {

    private final DonRepository donRepository;

    public DonServiceImpl(DonRepository donRepository) {
        this.donRepository = donRepository;
    }

    @Override
    @Transactional
    public Don addDon(Don don) {
        prepareRelations(don);
        return donRepository.save(don);
    }

    @Override
    @Transactional
    public Don updateDon(Long id, Don don) {
        retrieveDon(id);
        don.setId(id);
        prepareRelations(don);
        return donRepository.save(don);
    }

    @Override
    @Transactional
    public void deleteDon(Long id) {
        donRepository.delete(retrieveDon(id));
    }

    @Override
    public Don retrieveDon(Long id) {
        return donRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Don introuvable : " + id));
    }

    @Override
    public List<Don> retrieveAllDons() {
        return donRepository.findAll();
    }

    @Override
    public List<Don> retrieveMesDons(Long donateurId) {
        return donRepository.findByDonateurId(donateurId);
    }

    @Override
    public Page<Don> retrieveDons(Long pharmacieId, StatutDon statut, int page) {
        PageRequest pageable = PageRequest.of(page, 10);

        if (pharmacieId != null && statut != null) {
            return donRepository.findByPharmacieIdAndStatut(pharmacieId, statut, pageable);
        }
        if (pharmacieId != null) {
            return donRepository.findByPharmacieId(pharmacieId, pageable);
        }
        if (statut != null) {
            return donRepository.findByStatut(statut, pageable);
        }
        return donRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Don annulerDon(Long id) {
        Don don = retrieveDon(id);
        don.setStatut(StatutDon.ANNULE);
        return donRepository.save(don);
    }

    @Override
    @Transactional
    public Don prendreDecision(Long id, StatutDon statut) {
        if (statut != StatutDon.ACCEPTE && statut != StatutDon.REFUSE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La dÃ©cision doit Ãªtre ACCEPTE ou REFUSE");
        }

        Don don = retrieveDon(id);
        don.setStatut(statut);
        return donRepository.save(don);
    }

    private void prepareRelations(Don don) {
        ControleDon controleDon = don.getControleDon();
        if (controleDon != null) {
            controleDon.setDon(don);
        }

        if (don.getRemisesSolidaires() != null) {
            for (RemiseSolidaire remiseSolidaire : don.getRemisesSolidaires()) {
                remiseSolidaire.setDon(don);
            }
        }
    }
}
