package tn.esprit.donationservice.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.donationservice.entity.RemiseSolidaire;
import tn.esprit.donationservice.service.IRemiseSolidaireService;

import java.util.List;

@RestController
@RequestMapping("/api/remises-solidaires")
public class RemiseSolidaireController {

    private final IRemiseSolidaireService remiseSolidaireService;

    public RemiseSolidaireController(IRemiseSolidaireService remiseSolidaireService) {
        this.remiseSolidaireService = remiseSolidaireService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RemiseSolidaire addRemiseSolidaire(@Valid @RequestBody RemiseSolidaire remiseSolidaire) {
        return remiseSolidaireService.addRemiseSolidaire(remiseSolidaire);
    }

    @PutMapping("/{id}")
    public RemiseSolidaire updateRemiseSolidaire(@PathVariable Long id,
                                                  @Valid @RequestBody RemiseSolidaire remiseSolidaire) {
        return remiseSolidaireService.updateRemiseSolidaire(id, remiseSolidaire);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRemiseSolidaire(@PathVariable Long id) {
        remiseSolidaireService.deleteRemiseSolidaire(id);
    }

    @GetMapping("/{id}")
    public RemiseSolidaire retrieveRemiseSolidaire(@PathVariable Long id) {
        return remiseSolidaireService.retrieveRemiseSolidaire(id);
    }

    @GetMapping
    public List<RemiseSolidaire> retrieveAllRemisesSolidaires() {
        return remiseSolidaireService.retrieveAllRemisesSolidaires();
    }
}
