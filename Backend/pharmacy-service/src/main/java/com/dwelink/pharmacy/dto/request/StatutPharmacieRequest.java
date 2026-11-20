package com.dwelink.pharmacy.dto.request;

import jakarta.validation.constraints.NotNull;

public class StatutPharmacieRequest {
    @NotNull(message = "Le statut est obligatoire (EN_ATTENTE, VALIDEE, SUSPENDUE)")
    private String statut;

    public StatutPharmacieRequest() {}

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}
