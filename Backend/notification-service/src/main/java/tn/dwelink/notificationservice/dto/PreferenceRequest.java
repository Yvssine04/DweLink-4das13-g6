package tn.dwelink.notificationservice.dto;

import jakarta.validation.constraints.NotNull;

public record PreferenceRequest(
        @NotNull(message = "applicationActivee est obligatoire")
        Boolean applicationActivee,

        @NotNull(message = "emailActive est obligatoire")
        Boolean emailActive
) {
}
