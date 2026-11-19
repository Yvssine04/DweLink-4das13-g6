package tn.dwelink.catalogservice.service;

import tn.dwelink.catalogservice.dto.MedicamentRequest;
import tn.dwelink.catalogservice.dto.MedicamentResponse;

public interface IMedicamentService {
    MedicamentResponse creer(MedicamentRequest request);
    MedicamentResponse consulter(Long id);
}