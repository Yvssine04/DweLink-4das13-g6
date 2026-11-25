package tn.dwelink.notificationservice.dto;

import java.time.LocalDateTime;
import tn.dwelink.notificationservice.enums.StatutEnvoi;
import tn.dwelink.notificationservice.modele.Notification;

public record NotificationResponse(
        String id,
        Long destinataireId,
        String type,
        String titre,
        String message,
        boolean lue,
        StatutEnvoi statutEnvoi,
        LocalDateTime dateCreation
) {
    public static NotificationResponse depuis(Notification n) {
        return new NotificationResponse(n.getId(), n.getDestinataireId(), n.getType(), n.getTitre(),
                n.getMessage(), n.isLue(), n.getStatutEnvoi(), n.getDateCreation());
    }
}
