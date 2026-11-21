package tn.dwelink.catalogservice.controller;

import lombok.RequiredArgsConstructor;
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
    public PrincipeActifResponse creer(@RequestBody PrincipeActifRequest request) {
        return iPrincipeActifService.creer(request);
    }

    @GetMapping
    public List<PrincipeActifResponse> rechercher(@RequestParam(required = false) String q) {
        return iPrincipeActifService.rechercher(q);
    }
}
