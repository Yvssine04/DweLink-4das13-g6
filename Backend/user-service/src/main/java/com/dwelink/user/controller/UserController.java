package com.dwelink.user.controller;

import com.dwelink.user.dto.request.*;
import com.dwelink.user.dto.response.*;
import com.dwelink.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Utilisateurs & Profils", description = "Gestion des profils utilisateurs et demandes pharmaciens")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/utilisateurs/moi")
    @Operation(summary = "Consulter son profil", description = "Récupère les informations du compte connecté via JWT",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ProfilResponse> getMyProfile(Authentication authentication) {
        return ResponseEntity.ok(userService.getCurrentUserProfile(authentication.getName()));
    }

    @PatchMapping("/utilisateurs/moi")
    @Operation(summary = "Modifier son profil", description = "Met à jour prénom, nom, téléphone, langue, rayon de recherche",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ProfilResponse> updateMyProfile(Authentication authentication,
                                                          @RequestBody UpdateProfilRequest request) {
        return ResponseEntity.ok(userService.updateCurrentUserProfile(authentication.getName(), request));
    }

    @PostMapping("/demandes-pharmacien")
    @Operation(summary = "Déposer une demande d'inscription pharmacien", description = "Soumet les informations professionnelles",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<DemandePharmacienResponse> submitDemande(Authentication authentication,
                                                                   @Valid @RequestBody DemandePharmacienRequest request) {
        return new ResponseEntity<>(userService.submitDemandePharmacien(authentication.getName(), request), HttpStatus.CREATED);
    }

    @PostMapping("/demandes-pharmacien/{id}/justificatifs")
    @Operation(summary = "Joindre un justificatif à une demande", description = "Ajoute un diplôme, carte de l'ordre ou pièce d'identité",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<JustificatifResponse> addJustificatif(Authentication authentication,
                                                                @PathVariable Long id,
                                                                @Valid @RequestBody JustificatifRequest request) {
        return new ResponseEntity<>(userService.addJustificatifToDemande(id, authentication.getName(), request), HttpStatus.CREATED);
    }

    @GetMapping("/demandes-pharmacien")
    @Operation(summary = "Examiner les demandes d'inscription pharmacien", description = "Réservé aux administrateurs",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<DemandePharmacienResponse>> getDemandes(@RequestParam(required = false) String statut) {
        return ResponseEntity.ok(userService.getDemandesPharmacien(statut));
    }

    @PatchMapping("/demandes-pharmacien/{id}/decision")
    @Operation(summary = "Décider d'une demande pharmacien (Approuver / Refuser)", description = "Réservé aux administrateurs",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<DemandePharmacienResponse> decideDemande(Authentication authentication,
                                                                   @PathVariable Long id,
                                                                   @Valid @RequestBody DecisionDemandeRequest request) {
        return ResponseEntity.ok(userService.decideDemande(id, authentication.getName(), request));
    }

    @PatchMapping("/utilisateurs/{id}/statut")
    @Operation(summary = "Suspendre ou réactiver un compte utilisateur", description = "Réservé aux administrateurs",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ProfilResponse> updateAccountStatus(@PathVariable Long id,
                                                              @Valid @RequestBody StatutCompteRequest request) {
        return ResponseEntity.ok(userService.updateAccountStatus(id, request));
    }

    @GetMapping("/utilisateurs/{id}")
    @Operation(summary = "API interne: Vérifier statut et rôle utilisateur", description = "Appelé par pharmacy-service via Feign")
    public ResponseEntity<UserInternalResponse> getUserInternal(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserInternalById(id));
    }

    @GetMapping("/utilisateurs/{id}/contact")
    @Operation(summary = "API interne: Récupérer les coordonnées d'un utilisateur", description = "Pour notifications ou vérifications")
    public ResponseEntity<UserContactResponse> getUserContact(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserContactById(id));
    }
}
