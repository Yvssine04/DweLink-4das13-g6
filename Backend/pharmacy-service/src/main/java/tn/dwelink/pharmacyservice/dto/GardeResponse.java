package tn.dwelink.pharmacyservice.dto;

import java.time.LocalDateTime;
import tn.dwelink.pharmacyservice.entity.Garde;

public record GardeResponse(Long id, Long pharmacieId, LocalDateTime debut, LocalDateTime fin) {

    public static GardeResponse from(Garde g) {
        return new GardeResponse(g.getId(), g.getPharmacie().getId(), g.getDebut(), g.getFin());
    }
}
