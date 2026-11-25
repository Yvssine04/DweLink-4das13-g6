package tn.dwelink.notificationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record NotificationRequest(
        @NotNull(message = "Le destinataire est obligatoire")
        @Positive(message = "L'identifiant du destinataire doit être positif")
        Long destinataireId,

        @NotBlank(message = "Le type est obligatoire")
        @Size(max = 50, message = "Le type ne doit pas dépasser 50 caractères")
        String type,

        @NotBlank(message = "Le titre est obligatoire")
        @Size(max = 150, message = "Le titre ne doit pas dépasser 150 caractères")
        String titre,

        @NotBlank(message = "Le message est obligatoire")
        @Size(max = 1000, message = "Le message ne doit pas dépasser 1000 caractères")
        String message
) {
}
