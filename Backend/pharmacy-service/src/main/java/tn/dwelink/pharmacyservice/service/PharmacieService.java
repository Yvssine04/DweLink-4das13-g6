package tn.dwelink.pharmacyservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.dwelink.pharmacyservice.dto.PharmacieRequest;
import tn.dwelink.pharmacyservice.dto.PharmacieResponse;
import tn.dwelink.pharmacyservice.entity.MembrePharmacie;
import tn.dwelink.pharmacyservice.entity.Pharmacie;
import tn.dwelink.pharmacyservice.enums.RoleMembre;
import tn.dwelink.pharmacyservice.enums.StatutMembre;
import tn.dwelink.pharmacyservice.enums.StatutPharmacie;
import tn.dwelink.pharmacyservice.exception.AccesInterditException;
import tn.dwelink.pharmacyservice.exception.ConflitException;
import tn.dwelink.pharmacyservice.exception.RessourceIntrouvableException;
import tn.dwelink.pharmacyservice.repository.MembrePharmacieRepository;
import tn.dwelink.pharmacyservice.repository.PharmacieRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class PharmacieService {

    private final PharmacieRepository pharmacieRepository;
    private final MembrePharmacieRepository membreRepository;

    public PharmacieResponse creer(PharmacieRequest req, Long utilisateurId) {
        if (pharmacieRepository.existsByNumeroAutorisation(req.numeroAutorisation())) {
            throw new ConflitException("Numéro d'autorisation déjà utilisé : " + req.numeroAutorisation());
        }
        Pharmacie pharmacie = new Pharmacie();
        appliquer(pharmacie, req);
        pharmacie.setStatut(StatutPharmacie.EN_ATTENTE);

        MembrePharmacie titulaire = MembrePharmacie.builder()
                .utilisateurId(utilisateurId)
                .role(RoleMembre.TITULAIRE)
                .statut(StatutMembre.ACTIF)
                .pharmacie(pharmacie)
                .build();
        pharmacie.getMembres().add(titulaire);

        return PharmacieResponse.from(pharmacieRepository.save(pharmacie));
    }

    @Transactional(readOnly = true)
    public PharmacieResponse trouver(Long id) {
        return PharmacieResponse.from(charger(id));
    }

    public PharmacieResponse modifier(Long id, PharmacieRequest req, Long utilisateurId) {
        Pharmacie pharmacie = charger(id);
        if (!membreRepository.existsByPharmacieIdAndUtilisateurIdAndRoleAndStatut(
                id, utilisateurId, RoleMembre.TITULAIRE, StatutMembre.ACTIF)) {
            throw new AccesInterditException("Seul le titulaire actif peut modifier cette pharmacie");
        }
        if (!pharmacie.getNumeroAutorisation().equals(req.numeroAutorisation())
                && pharmacieRepository.existsByNumeroAutorisation(req.numeroAutorisation())) {
            throw new ConflitException("Numéro d'autorisation déjà utilisé : " + req.numeroAutorisation());
        }
        appliquer(pharmacie, req);
        return PharmacieResponse.from(pharmacie);
    }

    public PharmacieResponse changerStatut(Long id, StatutPharmacie statut) {
        Pharmacie pharmacie = charger(id);
        pharmacie.setStatut(statut);
        return PharmacieResponse.from(pharmacie);
    }

    private Pharmacie charger(Long id) {
        return pharmacieRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Pharmacie introuvable : " + id));
    }

    private void appliquer(Pharmacie p, PharmacieRequest req) {
        p.setNom(req.nom());
        p.setNumeroAutorisation(req.numeroAutorisation());
        p.setAdresse(req.adresse());
        p.setVille(req.ville());
        p.setLatitude(req.latitude());
        p.setLongitude(req.longitude());
        p.setTelephone(req.telephone());
    }
}
