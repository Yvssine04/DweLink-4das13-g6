package tn.dwelink.catalogservice.service;

import tn.dwelink.catalogservice.dto.MedicamentRequest;
import tn.dwelink.catalogservice.dto.MedicamentResponse;

import java.util.List;

public interface IMedicamentService {
    MedicamentResponse creer(MedicamentRequest request);
    MedicamentResponse consulter(Long id);

    List<MedicamentResponse> rechercher(String q, String categorie, String laboratoire);
}