package tn.dwelink.inventoryservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.dwelink.inventoryservice.dto.LotResponse;
import tn.dwelink.inventoryservice.dto.StatutLotRequest;
import tn.dwelink.inventoryservice.service.LotService;

@RestController                 // cette classe répond à des requêtes HTTP en JSON
@RequestMapping("/api/lots")    // toutes ses routes commencent par /api/lots
@RequiredArgsConstructor        // Lombok crée le constructeur qui injecte lotService
public class LotController {

    private final LotService lotService;

    // PATCH /api/lots/{id}/statut   corps : {"statut": "BLOQUE"}
    @PatchMapping("/{id}/statut")
    public LotResponse changerStatut(@PathVariable String id,
                                     @Valid @RequestBody StatutLotRequest req) {
        return lotService.changerStatut(id, req.statut());
    }
}