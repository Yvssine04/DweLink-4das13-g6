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
import tn.esprit.donationservice.entity.ControleDon;
import tn.esprit.donationservice.service.IControleDonService;

import java.util.List;

@RestController
@RequestMapping("/api/controles-dons")
public class ControleDonController {

    private final IControleDonService controleDonService;

    public ControleDonController(IControleDonService controleDonService) {
        this.controleDonService = controleDonService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ControleDon addControleDon(@Valid @RequestBody ControleDon controleDon) {
        return controleDonService.addControleDon(controleDon);
    }

    @PutMapping("/{id}")
    public ControleDon updateControleDon(@PathVariable Long id, @Valid @RequestBody ControleDon controleDon) {
        return controleDonService.updateControleDon(id, controleDon);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteControleDon(@PathVariable Long id) {
        controleDonService.deleteControleDon(id);
    }

    @GetMapping("/{id}")
    public ControleDon retrieveControleDon(@PathVariable Long id) {
        return controleDonService.retrieveControleDon(id);
    }

    @GetMapping
    public List<ControleDon> retrieveAllControleDons() {
        return controleDonService.retrieveAllControleDons();
    }
}
