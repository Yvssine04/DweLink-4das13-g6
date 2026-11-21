package tn.dwelink.pharmacyservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.dwelink.pharmacyservice.dto.GardeRequest;
import tn.dwelink.pharmacyservice.dto.GardeResponse;
import tn.dwelink.pharmacyservice.entity.Garde;
import tn.dwelink.pharmacyservice.entity.Pharmacie;
import tn.dwelink.pharmacyservice.exception.ConflitException;
import tn.dwelink.pharmacyservice.exception.RequeteInvalideException;
import tn.dwelink.pharmacyservice.repository.GardeRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class GardeService {

    private final PharmacieService pharmacieService;
    private final GardeRepository gardeRepository;

    public GardeResponse ajouter(Long pharmacieId, GardeRequest req, Long utilisateurId) {
        Pharmacie pharmacie = pharmacieService.charger(pharmacieId);
        pharmacieService.verifierTitulaire(pharmacieId, utilisateurId);

        if (!req.fin().isAfter(req.debut())) {
            throw new RequeteInvalideException("La fin de la garde doit être après son début");
        }
        if (gardeRepository.existsByPharmacieIdAndDebutLessThanAndFinGreaterThan(pharmacieId, req.fin(), req.debut())) {
            throw new ConflitException("Cette garde chevauche une garde existante");
        }

        Garde garde = Garde.builder()
                .debut(req.debut())
                .fin(req.fin())
                .pharmacie(pharmacie)
                .build();
        pharmacie.getGardes().add(garde);
        return GardeResponse.from(gardeRepository.save(garde));
    }
}
