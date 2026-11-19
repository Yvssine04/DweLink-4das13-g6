package tn.dwelink.pharmacyservice.dto;

import tn.dwelink.pharmacyservice.entity.MembrePharmacie;
import tn.dwelink.pharmacyservice.enums.RoleMembre;
import tn.dwelink.pharmacyservice.enums.StatutMembre;

public record MembreResponse(Long id, Long pharmacieId, Long utilisateurId, RoleMembre role, StatutMembre statut) {

    public static MembreResponse from(MembrePharmacie m) {
        return new MembreResponse(m.getId(), m.getPharmacie().getId(), m.getUtilisateurId(), m.getRole(), m.getStatut());
    }
}
