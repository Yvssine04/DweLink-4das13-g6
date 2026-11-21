package tn.dwelink.pharmacyservice.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.dwelink.pharmacyservice.dto.HoraireRequest;
import tn.dwelink.pharmacyservice.dto.HoraireResponse;
import tn.dwelink.pharmacyservice.entity.Horaire;
import tn.dwelink.pharmacyservice.entity.Pharmacie;
import tn.dwelink.pharmacyservice.exception.RequeteInvalideException;
import tn.dwelink.pharmacyservice.repository.PharmacieRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class HoraireService {

    private final PharmacieService pharmacieService;
    private final PharmacieRepository pharmacieRepository;

    public List<HoraireResponse> remplacer(Long pharmacieId, List<HoraireRequest> requetes, Long utilisateurId) {
        Pharmacie pharmacie = pharmacieService.charger(pharmacieId);
        pharmacieService.verifierTitulaire(pharmacieId, utilisateurId);

        for (HoraireRequest req : requetes) {
            if (!req.heureFermeture().isAfter(req.heureOuverture())) {
                throw new RequeteInvalideException("L'heure de fermeture doit être après l'heure d'ouverture ("
                        + req.jour() + ")");
            }
        }

        pharmacie.getHoraires().clear();
        for (HoraireRequest req : requetes) {
            pharmacie.getHoraires().add(Horaire.builder()
                    .jour(req.jour())
                    .heureOuverture(req.heureOuverture())
                    .heureFermeture(req.heureFermeture())
                    .pharmacie(pharmacie)
                    .build());
        }
        pharmacieRepository.saveAndFlush(pharmacie);

        return pharmacie.getHoraires().stream().map(HoraireResponse::from).toList();
    }
}
