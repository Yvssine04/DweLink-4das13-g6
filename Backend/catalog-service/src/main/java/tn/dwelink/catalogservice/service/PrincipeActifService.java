package tn.dwelink.catalogservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tn.dwelink.catalogservice.dto.PrincipeActifRequest;
import tn.dwelink.catalogservice.dto.PrincipeActifResponse;
import tn.dwelink.catalogservice.entity.PrincipeActif;
import tn.dwelink.catalogservice.repository.PrincipeActifRepository;

import java.util.List;
@Service
@RequiredArgsConstructor
public class PrincipeActifService implements IPrincipeActifService{
    final PrincipeActifRepository principeActifRepository;

    @Override
    public PrincipeActifResponse creer(PrincipeActifRequest request) {
        String nom = request.nomDci().trim();
        if (principeActifRepository.existsByNomDciIgnoreCase(nom)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce principe actif existe déjà");
        }
        PrincipeActif p = new PrincipeActif();
        p.setNomDci(nom);
        p = principeActifRepository.save(p);
        return new PrincipeActifResponse(p.getId(), p.getNomDci());
    }

    @Override
    public List<PrincipeActifResponse> rechercher(String q) {
        List<PrincipeActif> liste = (q == null || q.isBlank()) ? principeActifRepository.findAll() : principeActifRepository.findByNomDciContainingIgnoreCase(q.trim());
        return liste.stream()
                .map(p -> new PrincipeActifResponse(p.getId(), p.getNomDci()))
                .toList();
    }
}

