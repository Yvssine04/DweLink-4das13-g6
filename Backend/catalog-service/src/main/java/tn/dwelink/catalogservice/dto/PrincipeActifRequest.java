package tn.dwelink.catalogservice.dto;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.NotBlank;

public record PrincipeActifRequest(
        @NotBlank(message = "Le nom DCI est obligatoire") String nomDci) {

}

