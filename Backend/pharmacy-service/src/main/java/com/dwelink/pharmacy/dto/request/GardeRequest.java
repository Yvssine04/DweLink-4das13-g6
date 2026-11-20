package com.dwelink.pharmacy.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class GardeRequest {
    @NotNull(message = "La date de début est obligatoire")
    private LocalDateTime debut;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDateTime fin;

    public GardeRequest() {}

    public LocalDateTime getDebut() { return debut; }
    public void setDebut(LocalDateTime debut) { this.debut = debut; }

    public LocalDateTime getFin() { return fin; }
    public void setFin(LocalDateTime fin) { this.fin = fin; }
}
