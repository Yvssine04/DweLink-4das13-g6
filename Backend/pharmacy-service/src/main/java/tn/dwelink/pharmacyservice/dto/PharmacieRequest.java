package tn.dwelink.pharmacyservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PharmacieRequest(
        @NotBlank(message = "Le nom est obligatoire")
        @Size(max = 120, message = "Le nom ne doit pas dépasser 120 caractères")
        String nom,

        @NotBlank(message = "Le numéro d'autorisation est obligatoire")
        @Size(max = 30, message = "Le numéro d'autorisation ne doit pas dépasser 30 caractères")
        String numeroAutorisation,

        @Size(max = 200, message = "L'adresse ne doit pas dépasser 200 caractères")
        String adresse,

        @NotBlank(message = "La ville est obligatoire")
        @Size(max = 60, message = "La ville ne doit pas dépasser 60 caractères")
        String ville,

        Double latitude,

        Double longitude,

        @Size(max = 20, message = "Le téléphone ne doit pas dépasser 20 caractères")
        String telephone
) {
}
