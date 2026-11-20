package com.dwelink.pharmacy.service;

import com.dwelink.pharmacy.dto.request.*;
import com.dwelink.pharmacy.dto.response.*;

import java.util.List;

public interface PharmacyService {
    PharmacieResponse createPharmacie(CreatePharmacieRequest request);
    PharmacieResponse getPharmacieById(Long id);
    PharmacieResponse updatePharmacie(Long id, UpdatePharmacieRequest request);
    List<PharmacieResponse> searchPharmacies(String ville, Boolean deGarde);
    List<PharmacieResponse> getPharmaciesProches(Double lat, Double lng, Double rayonKm);
    PharmacieResponse updateStatutPharmacie(Long id, StatutPharmacieRequest request);

    List<HoraireResponse> updateHoraires(Long id, List<HoraireRequest> horaires);
    GardeResponse addGarde(Long id, GardeRequest request);

    MembrePharmacieResponse demandToJoinPharmacie(Long pharmacieId, DemandeMembreRequest request);
    MembrePharmacieResponse decideMembre(Long pharmacieId, Long membreId, DecisionMembreRequest request);
    void removeMembre(Long pharmacieId, Long membreId);
    MembreVerificationResponse verifyMembre(Long pharmacieId, Long utilisateurId);
}
