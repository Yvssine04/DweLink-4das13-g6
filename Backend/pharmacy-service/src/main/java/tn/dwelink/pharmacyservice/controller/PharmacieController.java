package tn.dwelink.pharmacyservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tn.dwelink.pharmacyservice.dto.PharmacieRequest;
import tn.dwelink.pharmacyservice.dto.PharmacieResponse;
import tn.dwelink.pharmacyservice.dto.StatutRequest;
import tn.dwelink.pharmacyservice.service.PharmacieService;

@RestController
@RequestMapping("/api/pharmacies")
@RequiredArgsConstructor
public class PharmacieController {

    private final PharmacieService pharmacieService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PharmacieResponse creer(@Valid @RequestBody PharmacieRequest req,
                                   @RequestHeader("X-Utilisateur-Id") Long utilisateurId) {
        return pharmacieService.creer(req, utilisateurId);
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
}
