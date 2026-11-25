package tn.dwelink.notificationservice.dto;

import tn.dwelink.notificationservice.modele.PreferenceNotification;

public record PreferenceResponse(
        Long utilisateurId,
        boolean applicationActivee,
        boolean emailActive
) {
    public static PreferenceResponse depuis(PreferenceNotification p) {
        return new PreferenceResponse(p.getUtilisateurId(), p.isApplicationActivee(), p.isEmailActive());
    }
}
