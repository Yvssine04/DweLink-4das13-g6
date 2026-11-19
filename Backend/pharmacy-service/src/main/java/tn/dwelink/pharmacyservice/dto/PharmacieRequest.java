package tn.dwelink.pharmacyservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PharmacieRequest(
        @NotBlank @Size(max = 120) String nom,
        @NotBlank @Size(max = 30) String numeroAutorisation,
        @Size(max = 200) String adresse,
        @NotBlank @Size(max = 60) String ville,
        Double latitude,
        Double longitude,
        @Size(max = 20) String telephone
) {
}
