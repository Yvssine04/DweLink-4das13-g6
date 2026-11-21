package com.dwelink.user.dto.request;

import jakarta.validation.constraints.NotNull;

public class DecisionDemandeRequest {
    @NotNull(message = "La décision est obligatoire (APPROUVEE ou REFUSEE)")
    private String decision; // APPROUVEE, REFUSEE

    private String commentaire;

    public DecisionDemandeRequest() {}

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
}
