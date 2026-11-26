package tn.dwelink.donationservice.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tn.dwelink.donationservice.dto.DecisionDonRequest;
import tn.dwelink.donationservice.entity.ControleDon;
import tn.dwelink.donationservice.entity.Don;
import tn.dwelink.donationservice.entity.RemiseSolidaire;
import tn.dwelink.donationservice.enums.StatutDon;
import tn.dwelink.donationservice.service.IControleDonService;
import tn.dwelink.donationservice.service.IDonService;
import tn.dwelink.donationservice.service.IRemiseSolidaireService;

import java.util.List;

@RestController
@RequestMapping("/api/dons")
public class DonController {

    private final IDonService donService;
    private final IControleDonService controleDonService;
    private final IRemiseSolidaireService remiseSolidaireService;

    public DonController(IDonService donService, IControleDonService controleDonService,
                         IRemiseSolidaireService remiseSolidaireService) {
        this.donService = donService;
        this.controleDonService = controleDonService;
        this.remiseSolidaireService = remiseSolidaireService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Don addDon(@Valid @RequestBody Don don) {
        return donService.addDon(don);
    }

    @PutMapping("/{id}")
    public Don updateDon(@PathVariable Long id, @Valid @RequestBody Don don) {
        return donService.updateDon(id, don);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDon(@PathVariable Long id) {
        donService.deleteDon(id);
    }

    @GetMapping("/{id}")
    public Don retrieveDon(@PathVariable Long id) {
        return donService.retrieveDon(id);
    }

    @GetMapping
    public Object retrieveDons(@RequestParam(required = false) Long pharmacieId,
                               @RequestParam(required = false) StatutDon statut,
                               @RequestParam(required = false) Integer page) {
        if (pharmacieId == null && statut == null && page == null) {
            return donService.retrieveAllDons();
        }
        return donService.retrieveDons(pharmacieId, statut, page == null ? 0 : page);
    }

    @GetMapping("/mes-dons")
    public List<Don> retrieveMesDons(@RequestParam Long donateurId) {
        return donService.retrieveMesDons(donateurId);
    }

    @PatchMapping("/{id}/annuler")
    public Don annulerDon(@PathVariable Long id) {
        return donService.annulerDon(id);
    }

    @PatchMapping("/{id}/decision")
    public Don prendreDecision(@PathVariable Long id, @Valid @RequestBody DecisionDonRequest decision) {
        return donService.prendreDecision(id, decision.getStatut());
    }

    @PostMapping("/{id}/controle")
    @ResponseStatus(HttpStatus.CREATED)
    public ControleDon addControleDon(@PathVariable Long id, @Valid @RequestBody ControleDon controleDon) {
        Don don = new Don();
        don.setId(id);
        controleDon.setDon(don);
        return controleDonService.addControleDon(controleDon);
    }

    @PostMapping("/{id}/remises")
    @ResponseStatus(HttpStatus.CREATED)
    public RemiseSolidaire addRemiseSolidaire(@PathVariable Long id,
                                               @Valid @RequestBody RemiseSolidaire remiseSolidaire) {
        Don don = new Don();
        don.setId(id);
        remiseSolidaire.setDon(don);
        return remiseSolidaireService.addRemiseSolidaire(remiseSolidaire);
    }

}
