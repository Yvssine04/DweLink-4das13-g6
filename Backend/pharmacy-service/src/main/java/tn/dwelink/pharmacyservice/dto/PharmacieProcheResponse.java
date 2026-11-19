package tn.dwelink.pharmacyservice.dto;

import tn.dwelink.pharmacyservice.entity.Pharmacie;
import tn.dwelink.pharmacyservice.enums.StatutPharmacie;

public record PharmacieProcheResponse(
        Long id,
        String nom,
        String numeroAutorisation,
        String adresse,
        String ville,
        Double latitude,
        Double longitude,
        String telephone,
        StatutPharmacie statut,
        double distanceKm
) {

    public static PharmacieProcheResponse from(Pharmacie p, double distanceKm) {
        return new PharmacieProcheResponse(p.getId(), p.getNom(), p.getNumeroAutorisation(), p.getAdresse(),
                p.getVille(), p.getLatitude(), p.getLongitude(), p.getTelephone(), p.getStatut(),
                Math.round(distanceKm * 100) / 100.0);
    }
}
