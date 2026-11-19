package tn.dwelink.pharmacyservice.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.dwelink.pharmacyservice.dto.PharmacieProcheResponse;
import tn.dwelink.pharmacyservice.dto.PharmacieRequest;
import tn.dwelink.pharmacyservice.dto.PharmacieResponse;
import tn.dwelink.pharmacyservice.entity.MembrePharmacie;
import tn.dwelink.pharmacyservice.entity.Pharmacie;
import tn.dwelink.pharmacyservice.enums.RoleMembre;
import tn.dwelink.pharmacyservice.evenement.PublicateurEvenements;
import tn.dwelink.pharmacyservice.evenement.StatutPharmacieModifie;
import tn.dwelink.pharmacyservice.enums.StatutMembre;
import tn.dwelink.pharmacyservice.enums.StatutPharmacie;
import tn.dwelink.pharmacyservice.exception.AccesInterditException;
import tn.dwelink.pharmacyservice.exception.ConflitException;
import tn.dwelink.pharmacyservice.exception.RessourceIntrouvableException;
import tn.dwelink.pharmacyservice.repository.GardeRepository;
import tn.dwelink.pharmacyservice.repository.MembrePharmacieRepository;
import tn.dwelink.pharmacyservice.repository.PharmacieRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class PharmacieService {

    private static final double RAYON_TERRE_KM = 6371.0;

    private final PharmacieRepository pharmacieRepository;
    private final MembrePharmacieRepository membreRepository;
    private final GardeRepository gardeRepository;
    private final PublicateurEvenements publicateurEvenements;

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
        verifierTitulaire(id, utilisateurId);
        if (!pharmacie.getNumeroAutorisation().equals(req.numeroAutorisation())
                && pharmacieRepository.existsByNumeroAutorisation(req.numeroAutorisation())) {
            throw new ConflitException("Numéro d'autorisation déjà utilisé : " + req.numeroAutorisation());
        }
        appliquer(pharmacie, req);
        return PharmacieResponse.from(pharmacie);
    }

    public PharmacieResponse changerStatut(Long id, StatutPharmacie statut) {
        Pharmacie pharmacie = charger(id);
        StatutPharmacie ancienStatut = pharmacie.getStatut();
        pharmacie.setStatut(statut);
        Pharmacie enregistree = pharmacieRepository.save(pharmacie);

        if (ancienStatut != statut) {
            publicateurEvenements.publierStatutPharmacieModifie(new StatutPharmacieModifie(
                    enregistree.getId(), enregistree.getNom(), ancienStatut, statut, LocalDateTime.now()));
        }
        return PharmacieResponse.from(enregistree);
    }

    @Transactional(readOnly = true)
    public List<PharmacieResponse> rechercher(String ville, boolean deGarde) {
        List<Pharmacie> pharmacies = (ville == null || ville.isBlank())
                ? pharmacieRepository.findByStatut(StatutPharmacie.VALIDEE)
                : pharmacieRepository.findByVilleIgnoreCaseAndStatut(ville.trim(), StatutPharmacie.VALIDEE);

        if (deGarde) {
            LocalDateTime maintenant = LocalDateTime.now();
            Set<Long> idsDeGarde = gardeRepository.findByDebutLessThanEqualAndFinGreaterThanEqual(maintenant, maintenant)
                    .stream()
                    .map(g -> g.getPharmacie().getId())
                    .collect(Collectors.toSet());
            pharmacies = pharmacies.stream().filter(p -> idsDeGarde.contains(p.getId())).toList();
        }
        return pharmacies.stream().map(PharmacieResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<PharmacieProcheResponse> proches(double lat, double lng, double rayonKm) {
        return pharmacieRepository.findByStatut(StatutPharmacie.VALIDEE).stream()
                .filter(p -> p.getLatitude() != null && p.getLongitude() != null)
                .map(p -> PharmacieProcheResponse.from(p, distanceKm(lat, lng, p.getLatitude(), p.getLongitude())))
                .filter(r -> r.distanceKm() <= rayonKm)
                .sorted(Comparator.comparingDouble(PharmacieProcheResponse::distanceKm))
                .toList();
    }

    Pharmacie charger(Long id) {
        return pharmacieRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Pharmacie introuvable : " + id));
    }

    void verifierTitulaire(Long pharmacieId, Long utilisateurId) {
        if (!membreRepository.existsByPharmacieIdAndUtilisateurIdAndRoleAndStatut(
                pharmacieId, utilisateurId, RoleMembre.TITULAIRE, StatutMembre.ACTIF)) {
            throw new AccesInterditException("Seul le titulaire actif peut modifier cette pharmacie");
        }
    }

    static double distanceKm(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return 2 * RAYON_TERRE_KM * Math.asin(Math.sqrt(a));
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
