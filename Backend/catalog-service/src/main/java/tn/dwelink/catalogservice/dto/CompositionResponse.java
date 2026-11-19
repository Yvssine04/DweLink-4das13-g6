package tn.dwelink.catalogservice.dto;


public record CompositionResponse(
        Long id, Long principeActifId, String nomDci, Double dosage, String unite) {}
