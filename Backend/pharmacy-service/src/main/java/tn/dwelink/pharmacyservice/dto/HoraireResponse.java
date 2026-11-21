package tn.dwelink.pharmacyservice.dto;

import java.time.LocalTime;
import tn.dwelink.pharmacyservice.entity.Horaire;
import tn.dwelink.pharmacyservice.enums.JourSemaine;

public record HoraireResponse(Long id, JourSemaine jour, LocalTime heureOuverture, LocalTime heureFermeture) {

    public static HoraireResponse from(Horaire h) {
        return new HoraireResponse(h.getId(), h.getJour(), h.getHeureOuverture(), h.getHeureFermeture());
    }
}
