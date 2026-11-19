package tn.dwelink.catalogservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.dwelink.catalogservice.dto.CompositionRequest;
import tn.dwelink.catalogservice.dto.MedicamentRequest;
import tn.dwelink.catalogservice.dto.MedicamentResponse;
import tn.dwelink.catalogservice.dto.MedicamentUpdateRequest;
import tn.dwelink.catalogservice.service.MedicamentService;

import java.util.List;

@RestController
@RequestMapping("/api/medicaments")
@RequiredArgsConstructor
public class MedicamentController {
    private final MedicamentService service;

    @PostMapping
    public MedicamentResponse creer(@Valid @RequestBody MedicamentRequest request) {
        return service.creer(request);
    }

    @GetMapping("/{id}")
    public MedicamentResponse consulter(@PathVariable Long id) {
        return service.consulter(id);
    }

    @GetMapping
    public List<MedicamentResponse> rechercher(@RequestParam(required = false) String q,
                                               @RequestParam(required = false) String categorie,
                                               @RequestParam(required = false) String laboratoire) {
        return service.rechercher(q, categorie, laboratoire);
    }

    @PutMapping("/{id}")
    public MedicamentResponse modifier(@PathVariable Long id,
                                       @RequestBody MedicamentUpdateRequest request) {
        return service.modifier(id, request);
    }

    @PostMapping("/{id}/compositions")
    public MedicamentResponse ajouterComposition(@PathVariable Long id,
                                                 @RequestBody CompositionRequest request) {
        return service.ajouterComposition(id, request);
    }

    @DeleteMapping("/{id}/compositions/{compositionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimerComposition(@PathVariable Long id, @PathVariable Long compositionId) {
        service.supprimerComposition(id, compositionId);
    }
}
