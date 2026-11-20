package com.dwelink.pharmacy.dto.request;

import jakarta.validation.constraints.NotNull;

public class DecisionMembreRequest {
    @NotNull(message = "La décision est obligatoire (ACTIF ou REFUSE)")
    private String decision;

    public DecisionMembreRequest() {}

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }
}
