package tn.dwelink.inventoryservice.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.dwelink.inventoryservice.dto.*;
import tn.dwelink.inventoryservice.service.ArticleStockService;
import tn.dwelink.inventoryservice.service.LotService;

import tn.dwelink.inventoryservice.service.MouvementService;
import tn.dwelink.inventoryservice.dto.MouvementRequest;
import tn.dwelink.inventoryservice.dto.MouvementResponse;


@RestController
@RequestMapping("/api/articles-stock")
@RequiredArgsConstructor
public class ArticleStockController {

    private final ArticleStockService articleService;
    private final LotService lotService;
    private final MouvementService mouvementService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArticleStockResponse creer(@Valid @RequestBody ArticleStockRequest req) {
        return articleService.creer(req);
    }

    @GetMapping
    public List<ArticleStockResponse> lister(@RequestParam Long pharmacieId) {
        return articleService.lister(pharmacieId);
    }

    @GetMapping("/{id}")
    public ArticleStockResponse trouver(@PathVariable String id) {
        return articleService.trouver(id);
    }

    @PatchMapping("/{id}")
    public ArticleStockResponse modifier(@PathVariable String id,
                                         @Valid @RequestBody ArticleStockUpdateRequest req) {
        return articleService.modifier(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable String id) {
        articleService.supprimer(id);
    }
    @PostMapping("/{id}/lots")
    @ResponseStatus(HttpStatus.CREATED)
    public LotResponse ajouterLot(@PathVariable String id, @Valid @RequestBody LotRequest req) {
        return lotService.ajouter(id, req);
    }
    @PostMapping("/{id}/mouvements")
    @ResponseStatus(HttpStatus.CREATED)
    public List<MouvementResponse> enregistrerMouvement(@PathVariable String id,
                                                        @Valid @RequestBody MouvementRequest req) {
        return mouvementService.enregistrer(id, req);
    }

    @GetMapping("/{id}/mouvements")
    public List<MouvementResponse> historique(@PathVariable String id) {
        return mouvementService.historique(id);
    }
}