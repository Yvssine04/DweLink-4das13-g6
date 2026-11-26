package tn.esprit.donationservice.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.donationservice.entity.ControleDon;
import tn.esprit.donationservice.entity.Don;
import tn.esprit.donationservice.repository.ControleDonRepository;
import tn.esprit.donationservice.repository.DonRepository;

import java.util.List;

@Service
public class ControleDonServiceImpl implements IControleDonService {

    private final ControleDonRepository controleDonRepository;
    private final DonRepository donRepository;

    public ControleDonServiceImpl(ControleDonRepository controleDonRepository, DonRepository donRepository) {
        this.controleDonRepository = controleDonRepository;
        this.donRepository = donRepository;
    }

    @Override
    @Transactional
    public ControleDon addControleDon(ControleDon controleDon) {
        resolveDon(controleDon);
        return controleDonRepository.save(controleDon);
    }

    @Override
    @Transactional
    public ControleDon updateControleDon(Long id, ControleDon controleDon) {
        retrieveControleDon(id);
        controleDon.setId(id);
        resolveDon(controleDon);
        return controleDonRepository.save(controleDon);
    }

    @Override
    @Transactional
    public void deleteControleDon(Long id) {
        ControleDon controleDon = retrieveControleDon(id);
        Don don = controleDon.getDon();

        if (don != null && don.getControleDon() != null
                && id.equals(don.getControleDon().getId())) {
            don.setControleDon(null);
        }

        controleDon.setDon(null);
        controleDonRepository.delete(controleDon);
        controleDonRepository.flush();
    }

    @Override
    public ControleDon retrieveControleDon(Long id) {
        return controleDonRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrôle de don introuvable : " + id));
    }

    @Override
    public List<ControleDon> retrieveAllControleDons() {
        return controleDonRepository.findAll();
    }

    private void resolveDon(ControleDon controleDon) {
        if (controleDon.getDon() == null || controleDon.getDon().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Un don existant est obligatoire");
        }

        Don don = donRepository.findById(controleDon.getDon().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Don introuvable"));
        controleDon.setDon(don);
    }
}
