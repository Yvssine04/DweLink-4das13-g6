package tn.dwelink.catalogservice.service;

import org.springframework.web.multipart.MultipartFile;
import tn.dwelink.catalogservice.dto.CompositionRequest;
import tn.dwelink.catalogservice.dto.MedicamentRequest;
import tn.dwelink.catalogservice.dto.MedicamentResponse;
import tn.dwelink.catalogservice.dto.MedicamentUpdateRequest;

import java.util.List;

public interface IMedicamentService {
    MedicamentResponse creer(MedicamentRequest request);
    MedicamentResponse consulter(Long id);

    List<MedicamentResponse> rechercher(String q, String categorie, String laboratoire);

    MedicamentResponse modifier(Long id, MedicamentUpdateRequest request);

    MedicamentResponse ajouterComposition(Long id, CompositionRequest request);

    void supprimerComposition(Long id, Long compositionId);

    List<MedicamentResponse> equivalents(Long id);

    MedicamentResponse retirer(Long id);

    public MedicamentResponse ajouterImage(Long id, MultipartFile file);

    void supprimerImage(Long id);
}