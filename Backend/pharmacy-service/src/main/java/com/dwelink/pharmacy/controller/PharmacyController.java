package com.dwelink.pharmacy.controller;

import com.dwelink.pharmacy.dto.request.*;
import com.dwelink.pharmacy.dto.response.*;
import com.dwelink.pharmacy.service.PharmacyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pharmacies")
@Tag(name = "Gestion des pharmacies", description = "Endpoints de gestion des officines, membres et horaires")
public class PharmacyController {

    private final PharmacyService pharmacyService;

    public PharmacyController(PharmacyService pharmacyService) {
        this.pharmacyService = pharmacyService;
    }

    @PostMapping
    @Operation(summary = "Créer une pharmacie", description = "Vérifie l'existence et le statut du pharmacien titulaire via OpenFeign avec user-service",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<PharmacieResponse> createPharmacie(@Valid @RequestBody CreatePharmacieRequest request) {
        return new ResponseEntity<>(pharmacyService.createPharmacie(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter une pharmacie", description = "Retourne les détails, horaires, gardes et membres d'une pharmacie")
    public ResponseEntity<PharmacieResponse> getPharmacieById(@PathVariable Long id) {
        return ResponseEntity.ok(pharmacyService.getPharmacieById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une pharmacie", description = "Réservé au pharmacien titulaire",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<PharmacieResponse> updatePharmacie(@PathVariable Long id, @RequestBody UpdatePharmacieRequest request) {
        return ResponseEntity.ok(pharmacyService.updatePharmacie(id, request));
    }

    @GetMapping
    @Operation(summary = "Rechercher des pharmacies", description = "Filtres optionnels par ville et statut de garde")
    public ResponseEntity<List<PharmacieResponse>> searchPharmacies(@RequestParam(required = false) String ville,
                                                                   @RequestParam(required = false) Boolean deGarde) {
        return ResponseEntity.ok(pharmacyService.searchPharmacies(ville, deGarde));
    }

    @GetMapping("/proches")
    @Operation(summary = "Trouver les pharmacies proches", description = "Recherche par coordonnées GPS et rayon en kilomètres")
    public ResponseEntity<List<PharmacieResponse>> getPharmaciesProches(@RequestParam Double lat,
                                                                       @RequestParam Double lng,
                                                                       @RequestParam(defaultValue = "10") Double rayonKm) {
        return ResponseEntity.ok(pharmacyService.getPharmaciesProches(lat, lng, rayonKm));
    }

    @PatchMapping("/{id}/statut")
    @Operation(summary = "Valider ou suspendre une pharmacie", description = "Réservé aux administrateurs",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<PharmacieResponse> updateStatut(@PathVariable Long id, @Valid @RequestBody StatutPharmacieRequest request) {
        return ResponseEntity.ok(pharmacyService.updateStatutPharmacie(id, request));
    }

    @PutMapping("/{id}/horaires")
    @Operation(summary = "Mettre à jour les horaires", description = "Définit les heures d'ouverture de la pharmacie",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<HoraireResponse>> updateHoraires(@PathVariable Long id, @RequestBody List<HoraireRequest> horaires) {
        return ResponseEntity.ok(pharmacyService.updateHoraires(id, horaires));
    }

    @PostMapping("/{id}/gardes")
    @Operation(summary = "Planifier une période de garde", description = "Définit un créneau de garde",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<GardeResponse> addGarde(@PathVariable Long id, @Valid @RequestBody GardeRequest request) {
        return new ResponseEntity<>(pharmacyService.addGarde(id, request), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/membres")
    @Operation(summary = "Demander à rejoindre la pharmacie", description = "Vérifie l'habilitation pharmacien via OpenFeign avec user-service",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<MembrePharmacieResponse> requestMembership(@PathVariable Long id, @Valid @RequestBody DemandeMembreRequest request) {
        return new ResponseEntity<>(pharmacyService.demandToJoinPharmacie(id, request), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/membres/{membreId}")
    @Operation(summary = "Accepter ou refuser un membre", description = "Réservé au pharmacien titulaire",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<MembrePharmacieResponse> decideMembership(@PathVariable Long id,
                                                                    @PathVariable Long membreId,
                                                                    @Valid @RequestBody DecisionMembreRequest request) {
        return ResponseEntity.ok(pharmacyService.decideMembre(id, membreId, request));
    }

    @DeleteMapping("/{id}/membres/{membreId}")
    @Operation(summary = "Retirer un membre de la pharmacie", description = "Réservé au pharmacien titulaire",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> removeMembership(@PathVariable Long id, @PathVariable Long membreId) {
        pharmacyService.removeMembre(id, membreId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/membres/verifier")
    @Operation(summary = "API interne: Vérifier si un utilisateur est membre actif", description = "Appelé par les services de gestion de stock et réservations")
    public ResponseEntity<MembreVerificationResponse> verifyMember(@PathVariable Long id, @RequestParam Long utilisateurId) {
        return ResponseEntity.ok(pharmacyService.verifyMembre(id, utilisateurId));
    }
}
