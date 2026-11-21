package tn.dwelink.pharmacyservice.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tn.dwelink.pharmacyservice.dto.DecisionMembreRequest;
import tn.dwelink.pharmacyservice.dto.GardeRequest;
import tn.dwelink.pharmacyservice.dto.GardeResponse;
import tn.dwelink.pharmacyservice.dto.HoraireRequest;
import tn.dwelink.pharmacyservice.dto.HoraireResponse;
import tn.dwelink.pharmacyservice.dto.MembreResponse;
import tn.dwelink.pharmacyservice.dto.PharmacieProcheResponse;
import tn.dwelink.pharmacyservice.dto.PharmacieRequest;
import tn.dwelink.pharmacyservice.dto.PharmacieResponse;
import tn.dwelink.pharmacyservice.dto.StatutRequest;
import tn.dwelink.pharmacyservice.dto.VerificationMembreResponse;
import tn.dwelink.pharmacyservice.service.GardeService;
import tn.dwelink.pharmacyservice.service.HoraireService;
import tn.dwelink.pharmacyservice.service.MembreService;
import tn.dwelink.pharmacyservice.service.PharmacieService;

@RestController
@RequestMapping("/api/pharmacies")
@RequiredArgsConstructor
public class PharmacieController {

    private final PharmacieService pharmacieService;
    private final HoraireService horaireService;
    private final GardeService gardeService;
    private final MembreService membreService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PharmacieResponse creer(@Valid @RequestBody PharmacieRequest req,
                                   @RequestHeader("X-Utilisateur-Id") Long utilisateurId) {
        return pharmacieService.creer(req, utilisateurId);
    }

    @GetMapping
    public List<PharmacieResponse> rechercher(@RequestParam(required = false) String ville,
                                              @RequestParam(defaultValue = "false") boolean deGarde) {
        return pharmacieService.rechercher(ville, deGarde);
    }

    @GetMapping("/proches")
    public List<PharmacieProcheResponse> proches(@RequestParam double lat,
                                                 @RequestParam double lng,
                                                 @RequestParam(defaultValue = "5") double rayonKm) {
        return pharmacieService.proches(lat, lng, rayonKm);
    }

    @GetMapping("/{id}")
    public PharmacieResponse trouver(@PathVariable Long id) {
        return pharmacieService.trouver(id);
    }

    @PutMapping("/{id}")
    public PharmacieResponse modifier(@PathVariable Long id,
                                      @Valid @RequestBody PharmacieRequest req,
                                      @RequestHeader("X-Utilisateur-Id") Long utilisateurId) {
        return pharmacieService.modifier(id, req, utilisateurId);
    }

    @PatchMapping("/{id}/statut")
    public PharmacieResponse changerStatut(@PathVariable Long id, @Valid @RequestBody StatutRequest req) {
        return pharmacieService.changerStatut(id, req.statut());
    }

    @PutMapping("/{id}/horaires")
    public List<HoraireResponse> remplacerHoraires(@PathVariable Long id,
                                                   @RequestBody List<@Valid HoraireRequest> horaires,
                                                   @RequestHeader("X-Utilisateur-Id") Long utilisateurId) {
        return horaireService.remplacer(id, horaires, utilisateurId);
    }

    @PostMapping("/{id}/gardes")
    @ResponseStatus(HttpStatus.CREATED)
    public GardeResponse ajouterGarde(@PathVariable Long id,
                                      @Valid @RequestBody GardeRequest req,
                                      @RequestHeader("X-Utilisateur-Id") Long utilisateurId) {
        return gardeService.ajouter(id, req, utilisateurId);
    }

    @PostMapping("/{id}/membres")
    @ResponseStatus(HttpStatus.CREATED)
    public MembreResponse demanderAdhesion(@PathVariable Long id,
                                           @RequestHeader("X-Utilisateur-Id") Long utilisateurId) {
        return membreService.demander(id, utilisateurId);
    }

    @GetMapping("/{id}/membres")
    public List<MembreResponse> listerMembres(@PathVariable Long id,
                                              @RequestHeader("X-Utilisateur-Id") Long utilisateurId) {
        return membreService.lister(id, utilisateurId);
    }

    @GetMapping("/{id}/membres/verifier")
    public VerificationMembreResponse verifierMembre(@PathVariable Long id, @RequestParam Long utilisateurId) {
        return membreService.verifier(id, utilisateurId);
    }

    @PatchMapping("/{id}/membres/{membreId}")
    public MembreResponse deciderMembre(@PathVariable Long id,
                                        @PathVariable Long membreId,
                                        @Valid @RequestBody DecisionMembreRequest req,
                                        @RequestHeader("X-Utilisateur-Id") Long utilisateurId) {
        return membreService.decider(id, membreId, req.statut(), utilisateurId);
    }

    @DeleteMapping("/{id}/membres/{membreId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void retirerMembre(@PathVariable Long id,
                              @PathVariable Long membreId,
                              @RequestHeader("X-Utilisateur-Id") Long utilisateurId) {
        membreService.retirer(id, membreId, utilisateurId);
    }
}
