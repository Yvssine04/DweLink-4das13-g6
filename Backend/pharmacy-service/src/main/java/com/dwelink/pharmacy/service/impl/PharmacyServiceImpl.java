package com.dwelink.pharmacy.service.impl;

import com.dwelink.pharmacy.client.UserClient;
import com.dwelink.pharmacy.client.dto.UserInternalResponse;
import com.dwelink.pharmacy.dto.request.*;
import com.dwelink.pharmacy.dto.response.*;
import com.dwelink.pharmacy.entity.*;
import com.dwelink.pharmacy.repository.GardeRepository;
import com.dwelink.pharmacy.repository.HoraireRepository;
import com.dwelink.pharmacy.repository.MembrePharmacieRepository;
import com.dwelink.pharmacy.repository.PharmacieRepository;
import com.dwelink.pharmacy.service.PharmacyService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class PharmacyServiceImpl implements PharmacyService {

    private final PharmacieRepository pharmacieRepository;
    private final MembrePharmacieRepository membreRepository;
    private final HoraireRepository horaireRepository;
    private final GardeRepository gardeRepository;
    private final UserClient userClient;

    public PharmacyServiceImpl(PharmacieRepository pharmacieRepository,
                               MembrePharmacieRepository membreRepository,
                               HoraireRepository horaireRepository,
                               GardeRepository gardeRepository,
                               UserClient userClient) {
        this.pharmacieRepository = pharmacieRepository;
        this.membreRepository = membreRepository;
        this.horaireRepository = horaireRepository;
        this.gardeRepository = gardeRepository;
        this.userClient = userClient;
    }

    @Override
    public PharmacieResponse createPharmacie(CreatePharmacieRequest request) {
        if (pharmacieRepository.existsByNumeroAutorisation(request.getNumeroAutorisation())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Une pharmacie existe déjà avec ce numéro d'autorisation");
        }

        UserInternalResponse user = userClient.getUserById(request.getTitulaireId());
        if (user == null || !"PHARMACIEN".equalsIgnoreCase(user.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "L'utilisateur spécifié n'est pas un pharmacien enregistré");
        }
        if (!"ACTIF".equalsIgnoreCase(user.getStatut())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Le compte pharmacien n'est pas encore validé ou actif");
        }

        Pharmacie pharmacie = new Pharmacie();
        pharmacie.setNom(request.getNom());
        pharmacie.setNumeroAutorisation(request.getNumeroAutorisation());
        pharmacie.setAdresse(request.getAdresse());
        pharmacie.setVille(request.getVille());
        pharmacie.setLatitude(request.getLatitude());
        pharmacie.setLongitude(request.getLongitude());
        pharmacie.setTelephone(request.getTelephone());
        pharmacie.setStatut(StatutPharmacie.EN_ATTENTE);

        Pharmacie savedPharmacie = pharmacieRepository.save(pharmacie);

        MembrePharmacie membre = new MembrePharmacie();
        membre.setPharmacie(savedPharmacie);
        membre.setUtilisateurId(request.getTitulaireId());
        membre.setRole(RoleMembre.TITULAIRE);
        membre.setStatut(StatutMembre.ACTIF);
        membreRepository.save(membre);

        savedPharmacie.getMembres().add(membre);
        return mapToPharmacieResponse(savedPharmacie);
    }

    @Override
    @Transactional(readOnly = true)
    public PharmacieResponse getPharmacieById(Long id) {
        Pharmacie pharmacie = pharmacieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pharmacie introuvable"));
        return mapToPharmacieResponse(pharmacie);
    }

    @Override
    public PharmacieResponse updatePharmacie(Long id, UpdatePharmacieRequest request) {
        Pharmacie pharmacie = pharmacieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pharmacie introuvable"));

        if (StringUtils.hasText(request.getNom())) pharmacie.setNom(request.getNom());
        if (StringUtils.hasText(request.getAdresse())) pharmacie.setAdresse(request.getAdresse());
        if (StringUtils.hasText(request.getVille())) pharmacie.setVille(request.getVille());
        if (request.getLatitude() != null) pharmacie.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) pharmacie.setLongitude(request.getLongitude());
        if (StringUtils.hasText(request.getTelephone())) pharmacie.setTelephone(request.getTelephone());

        return mapToPharmacieResponse(pharmacieRepository.save(pharmacie));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PharmacieResponse> searchPharmacies(String ville, Boolean deGarde) {
        List<Pharmacie> list;
        if (StringUtils.hasText(ville)) {
            list = pharmacieRepository.findByVilleIgnoreCase(ville);
        } else {
            list = pharmacieRepository.findAll();
        }

        if (Boolean.TRUE.equals(deGarde)) {
            LocalDateTime now = LocalDateTime.now();
            List<Garde> gardesActuelles = gardeRepository.findByDebutLessThanEqualAndFinGreaterThanEqual(now, now);
            Set<Long> pharmaIdsDeGarde = gardesActuelles.stream()
                    .map(g -> g.getPharmacie().getId())
                    .collect(Collectors.toSet());

            list = list.stream()
                    .filter(p -> pharmaIdsDeGarde.contains(p.getId()))
                    .collect(Collectors.toList());
        }

        return list.stream().map(this::mapToPharmacieResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PharmacieResponse> getPharmaciesProches(Double lat, Double lng, Double rayonKm) {
        if (lat == null || lng == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Latitude et longitude requises");
        }
        double limitKm = (rayonKm != null && rayonKm > 0) ? rayonKm : 10.0;

        return pharmacieRepository.findAll().stream()
                .filter(p -> p.getLatitude() != null && p.getLongitude() != null)
                .filter(p -> calculateDistanceKm(lat, lng, p.getLatitude(), p.getLongitude()) <= limitKm)
                .map(this::mapToPharmacieResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PharmacieResponse updateStatutPharmacie(Long id, StatutPharmacieRequest request) {
        Pharmacie pharmacie = pharmacieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pharmacie introuvable"));

        try {
            StatutPharmacie statut = StatutPharmacie.valueOf(request.getStatut().toUpperCase());
            pharmacie.setStatut(statut);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Statut de pharmacie invalide: " + request.getStatut());
        }

        return mapToPharmacieResponse(pharmacieRepository.save(pharmacie));
    }

    @Override
    public List<HoraireResponse> updateHoraires(Long id, List<HoraireRequest> horaires) {
        Pharmacie pharmacie = pharmacieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pharmacie introuvable"));

        horaireRepository.deleteByPharmacieId(id);

        List<Horaire> entities = horaires.stream().map(req -> {
            Horaire h = new Horaire();
            h.setPharmacie(pharmacie);
            h.setJour(JourSemaine.valueOf(req.getJour().toUpperCase()));
            h.setHeureOuverture(req.getHeureOuverture());
            h.setHeureFermeture(req.getHeureFermeture());
            return h;
        }).collect(Collectors.toList());

        List<Horaire> saved = horaireRepository.saveAll(entities);
        return saved.stream()
                .map(h -> new HoraireResponse(h.getId(), h.getJour().name(), h.getHeureOuverture(), h.getHeureFermeture()))
                .collect(Collectors.toList());
    }

    @Override
    public GardeResponse addGarde(Long id, GardeRequest request) {
        Pharmacie pharmacie = pharmacieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pharmacie introuvable"));

        if (request.getFin().isBefore(request.getDebut())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La date de fin doit être postérieure à la date de début");
        }

        Garde garde = new Garde();
        garde.setPharmacie(pharmacie);
        garde.setDebut(request.getDebut());
        garde.setFin(request.getFin());

        Garde saved = gardeRepository.save(garde);
        return new GardeResponse(saved.getId(), saved.getDebut(), saved.getFin());
    }

    @Override
    public MembrePharmacieResponse demandToJoinPharmacie(Long pharmacieId, DemandeMembreRequest request) {
        Pharmacie pharmacie = pharmacieRepository.findById(pharmacieId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pharmacie introuvable"));

        UserInternalResponse user = userClient.getUserById(request.getUtilisateurId());
        if (user == null || !"PHARMACIEN".equalsIgnoreCase(user.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Seul un pharmacien peut rejoindre une pharmacie");
        }
        if (!"ACTIF".equalsIgnoreCase(user.getStatut())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Le compte du pharmacien n'est pas actif");
        }

        if (membreRepository.findByPharmacieIdAndUtilisateurId(pharmacieId, request.getUtilisateurId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "L'utilisateur est déjà membre ou a une demande en cours");
        }

        RoleMembre role = RoleMembre.ASSISTANT;
        if (StringUtils.hasText(request.getRole())) {
            try {
                role = RoleMembre.valueOf(request.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rôle membre invalide");
            }
        }

        MembrePharmacie membre = new MembrePharmacie();
        membre.setPharmacie(pharmacie);
        membre.setUtilisateurId(request.getUtilisateurId());
        membre.setRole(role);
        membre.setStatut(StatutMembre.EN_ATTENTE);

        MembrePharmacie saved = membreRepository.save(membre);
        return new MembrePharmacieResponse(saved.getId(), pharmacieId, saved.getUtilisateurId(), saved.getRole().name(), saved.getStatut().name(), saved.getDateCreation());
    }

    @Override
    public MembrePharmacieResponse decideMembre(Long pharmacieId, Long membreId, DecisionMembreRequest request) {
        MembrePharmacie membre = membreRepository.findById(membreId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Membre introuvable"));

        if (!membre.getPharmacie().getId().equals(pharmacieId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le membre n'appartient pas à cette pharmacie");
        }

        try {
            StatutMembre statut = StatutMembre.valueOf(request.getDecision().toUpperCase());
            if (statut == StatutMembre.EN_ATTENTE) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Décision invalide");
            }
            membre.setStatut(statut);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Décision invalide: " + request.getDecision());
        }

        MembrePharmacie saved = membreRepository.save(membre);
        return new MembrePharmacieResponse(saved.getId(), pharmacieId, saved.getUtilisateurId(), saved.getRole().name(), saved.getStatut().name(), saved.getDateCreation());
    }

    @Override
    public void removeMembre(Long pharmacieId, Long membreId) {
        MembrePharmacie membre = membreRepository.findById(membreId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Membre introuvable"));

        if (!membre.getPharmacie().getId().equals(pharmacieId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le membre n'appartient pas à cette pharmacie");
        }

        membreRepository.delete(membre);
    }

    @Override
    @Transactional(readOnly = true)
    public MembreVerificationResponse verifyMembre(Long pharmacieId, Long utilisateurId) {
        var opt = membreRepository.findByPharmacieIdAndUtilisateurId(pharmacieId, utilisateurId);
        boolean actif = opt.isPresent() && opt.get().getStatut() == StatutMembre.ACTIF;
        String role = opt.map(m -> m.getRole().name()).orElse(null);
        return new MembreVerificationResponse(pharmacieId, utilisateurId, actif, role);
    }

    private PharmacieResponse mapToPharmacieResponse(Pharmacie p) {
        PharmacieResponse res = new PharmacieResponse();
        res.setId(p.getId());
        res.setNom(p.getNom());
        res.setNumeroAutorisation(p.getNumeroAutorisation());
        res.setAdresse(p.getAdresse());
        res.setVille(p.getVille());
        res.setLatitude(p.getLatitude());
        res.setLongitude(p.getLongitude());
        res.setTelephone(p.getTelephone());
        res.setStatut(p.getStatut().name());
        res.setDateCreation(p.getDateCreation());

        if (p.getMembres() != null) {
            res.setMembres(p.getMembres().stream()
                    .map(m -> new MembrePharmacieResponse(m.getId(), p.getId(), m.getUtilisateurId(), m.getRole().name(), m.getStatut().name(), m.getDateCreation()))
                    .collect(Collectors.toList()));
        }
        if (p.getHoraires() != null) {
            res.setHoraires(p.getHoraires().stream()
                    .map(h -> new HoraireResponse(h.getId(), h.getJour().name(), h.getHeureOuverture(), h.getHeureFermeture()))
                    .collect(Collectors.toList()));
        }
        if (p.getGardes() != null) {
            res.setGardes(p.getGardes().stream()
                    .map(g -> new GardeResponse(g.getId(), g.getDebut(), g.getFin()))
                    .collect(Collectors.toList()));
        }

        return res;
    }

    private double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
