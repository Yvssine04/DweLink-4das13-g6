package tn.dwelink.catalogservice.controller;

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
@RequiredArgsConstructor
public class PrincipeActifController {

    private final IPrincipeActifService iPrincipeActifService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PrincipeActifResponse creer(@Valid @RequestBody PrincipeActifRequest request) {
        return iPrincipeActifService.creer(request);
    }

    @GetMapping
    public List<PrincipeActifResponse> rechercher(@RequestParam(required = false) String q) {
        return iPrincipeActifService.rechercher(q);
    }
}