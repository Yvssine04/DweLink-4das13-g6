package tn.dwelink.pharmacyservice.dto;

import tn.dwelink.pharmacyservice.entity.Pharmacie;
import tn.dwelink.pharmacyservice.enums.StatutPharmacie;

public record PharmacieResponse(
        Long id,
        String nom,
        String numeroAutorisation,
        String adresse,
        String ville,
        Double latitude,
        Double longitude,
        String telephone,
        StatutPharmacie statut
) {

    public static PharmacieResponse from(Pharmacie p) {
        return new PharmacieResponse(p.getId(), p.getNom(), p.getNumeroAutorisation(), p.getAdresse(),
                p.getVille(), p.getLatitude(), p.getLongitude(), p.getTelephone(), p.getStatut());
    }
}
