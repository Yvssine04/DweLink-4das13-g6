package tn.dwelink.donationservice.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tn.dwelink.donationservice.entity.Don;
import tn.dwelink.donationservice.entity.RemiseSolidaire;
import tn.dwelink.donationservice.repository.DonRepository;
import tn.dwelink.donationservice.repository.RemiseSolidaireRepository;

import java.util.List;

@Service
public class RemiseSolidaireServiceImpl implements IRemiseSolidaireService {

    private final RemiseSolidaireRepository remiseSolidaireRepository;
    private final DonRepository donRepository;

    public RemiseSolidaireServiceImpl(RemiseSolidaireRepository remiseSolidaireRepository, DonRepository donRepository) {
        this.remiseSolidaireRepository = remiseSolidaireRepository;
        this.donRepository = donRepository;
    }

    @Override
    @Transactional
    public RemiseSolidaire addRemiseSolidaire(RemiseSolidaire remiseSolidaire) {
        resolveDon(remiseSolidaire);
        return remiseSolidaireRepository.save(remiseSolidaire);
    }

    @Override
    @Transactional
    public RemiseSolidaire updateRemiseSolidaire(Long id, RemiseSolidaire remiseSolidaire) {
        retrieveRemiseSolidaire(id);
        remiseSolidaire.setId(id);
        resolveDon(remiseSolidaire);
        return remiseSolidaireRepository.save(remiseSolidaire);
    }

    @Override
    @Transactional
    public void deleteRemiseSolidaire(Long id) {
        RemiseSolidaire remiseSolidaire = retrieveRemiseSolidaire(id);
        Don don = remiseSolidaire.getDon();

        if (don != null) {
            don.getRemisesSolidaires().removeIf(remise -> id.equals(remise.getId()));
        }

        remiseSolidaire.setDon(null);
        remiseSolidaireRepository.delete(remiseSolidaire);
        remiseSolidaireRepository.flush();
    }

    @Override
    public RemiseSolidaire retrieveRemiseSolidaire(Long id) {
        return remiseSolidaireRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Remise solidaire introuvable : " + id));
    }

    @Override
    public List<RemiseSolidaire> retrieveAllRemisesSolidaires() {
        return remiseSolidaireRepository.findAll();
    }

    private void resolveDon(RemiseSolidaire remiseSolidaire) {
        if (remiseSolidaire.getDon() == null || remiseSolidaire.getDon().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Un don existant est obligatoire");
        }

        Don don = donRepository.findById(remiseSolidaire.getDon().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Don introuvable"));
        remiseSolidaire.setDon(don);
    }
}
