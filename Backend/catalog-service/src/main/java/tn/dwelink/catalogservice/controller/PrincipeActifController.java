package tn.dwelink.catalogservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.dwelink.catalogservice.dto.PrincipeActifRequest;
import tn.dwelink.catalogservice.dto.PrincipeActifResponse;
import tn.dwelink.catalogservice.service.IPrincipeActifService;

import java.util.List;

@RestController
@RequestMapping("/api/principes-actifs")
@Tag(name = "Principes actifs", description = "Gestion des principes actifs")
@RequiredArgsConstructor
public class PrincipeActifController {

    private final IPrincipeActifService iPrincipeActifService;
    @Operation(summary = "Creer un principe actif")
    @ApiResponse(responseCode = "201", description = "Principe actif cree")
    @ApiResponse(responseCode = "409", description = "Principe actif deja existant")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PrincipeActifResponse creer(@Valid @RequestBody PrincipeActifRequest request) {
        return iPrincipeActifService.creer(request);
    }

    @Operation(summary = "Rechercher un principe actif")
    @ApiResponse(responseCode = "200", description = "Liste des principes actifs")
    @GetMapping
    public List<PrincipeActifResponse> rechercher(@RequestParam(required = false) String q) {
        return iPrincipeActifService.rechercher(q);
    }
}