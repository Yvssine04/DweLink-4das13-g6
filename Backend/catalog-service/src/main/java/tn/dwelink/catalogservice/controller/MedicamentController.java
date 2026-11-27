package tn.dwelink.catalogservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.dwelink.catalogservice.dto.CompositionRequest;
import tn.dwelink.catalogservice.dto.MedicamentRequest;
import tn.dwelink.catalogservice.dto.MedicamentResponse;
import tn.dwelink.catalogservice.dto.MedicamentUpdateRequest;
import tn.dwelink.catalogservice.service.IMedicamentService;

import java.util.List;

@RestController
@RequestMapping("/api/medicaments")
@Tag(name = "Medicaments", description = "Gestion du catalogue des medicaments")
@RequiredArgsConstructor
public class MedicamentController {

    private final IMedicamentService service;

    @Operation(summary = "Creer un medicament avec sa composition")
    @ApiResponse(responseCode = "201", description = "Medicament cree")
    @ApiResponse(responseCode = "400", description = "Donnees invalides")
    @ApiResponse(responseCode = "409", description = "Code-barres deja utilise")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MedicamentResponse creer(@Valid @RequestBody MedicamentRequest request) {
        return service.creer(request);
    }

    @Operation(summary = "Consulter un medicament et sa composition")
    @ApiResponse(responseCode = "200", description = "Medicament trouve")
    @ApiResponse(responseCode = "404", description = "Medicament introuvable")
    @GetMapping("/{id}")
    public MedicamentResponse consulter(@PathVariable Long id) {
        return service.consulter(id);
    }

    @Operation(summary = "Rechercher des medicaments par nom, categorie ou laboratoire")
    @ApiResponse(responseCode = "200", description = "Liste des medicaments")
    @GetMapping
    public List<MedicamentResponse> rechercher(@RequestParam(required = false) String q,
                                               @RequestParam(required = false) String categorie,
                                               @RequestParam(required = false) String laboratoire) {
        return service.rechercher(q, categorie, laboratoire);
    }

    @Operation(summary = "Modifier la fiche d'un medicament")
    @ApiResponse(responseCode = "200", description = "Medicament modifie")
    @ApiResponse(responseCode = "400", description = "Donnees invalides")
    @ApiResponse(responseCode = "404", description = "Medicament introuvable")
    @PutMapping("/{id}")
    public MedicamentResponse modifier(@PathVariable Long id,
                                       @Valid @RequestBody MedicamentUpdateRequest request) {
        return service.modifier(id, request);
    }

    @Operation(summary = "Ajouter un principe actif et son dosage")
    @ApiResponse(responseCode = "201", description = "Composition ajoutee")
    @ApiResponse(responseCode = "404", description = "Medicament introuvable")
    @PostMapping("/{id}/compositions")
    @ResponseStatus(HttpStatus.CREATED)
    public MedicamentResponse ajouterComposition(@PathVariable Long id,
                                                 @Valid @RequestBody CompositionRequest request) {
        return service.ajouterComposition(id, request);
    }

    @Operation(summary = "Retirer une composition (pas la derniere)")
    @ApiResponse(responseCode = "204", description = "Composition retiree")
    @ApiResponse(responseCode = "404", description = "Medicament ou composition introuvable")
    @ApiResponse(responseCode = "409", description = "Derniere composition du medicament")
    @DeleteMapping("/{id}/compositions/{compositionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimerComposition(@PathVariable Long id, @PathVariable Long compositionId) {
        service.supprimerComposition(id, compositionId);
    }

    @Operation(summary = "Consulter les medicaments equivalents")
    @ApiResponse(responseCode = "200", description = "Liste des equivalents")
    @ApiResponse(responseCode = "404", description = "Medicament introuvable")
    @GetMapping("/{id}/equivalents")
    public List<MedicamentResponse> equivalents(@PathVariable Long id) {
        return service.equivalents(id);
    }

    @Operation(summary = "Retirer un medicament du marche")
    @ApiResponse(responseCode = "200", description = "Medicament retire")
    @ApiResponse(responseCode = "404", description = "Medicament introuvable")
    @ApiResponse(responseCode = "409", description = "Medicament deja retire")
    @PatchMapping("/{id}/retrait")
    public MedicamentResponse retirer(@PathVariable Long id) {
        return service.retirer(id);
    }

    @Operation(summary = "Ajouter l'image d'un medicament")
    @ApiResponse(responseCode = "200", description = "Image enregistree")
    @PostMapping("/{id}/image")
    public MedicamentResponse ajouterImage(@PathVariable Long id,
                                           @RequestParam("file") MultipartFile file) {
        return service.ajouterImage(id, file);
    }

    @Operation(summary = "Supprimer l'image d'un medicament")
    @ApiResponse(responseCode = "204", description = "Image supprimee")
    @DeleteMapping("/{id}/image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimerImage(@PathVariable Long id) {
        service.supprimerImage(id);
    }
}