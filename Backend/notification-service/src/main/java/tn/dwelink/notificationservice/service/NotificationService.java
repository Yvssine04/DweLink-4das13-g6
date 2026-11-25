package tn.dwelink.notificationservice.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.dwelink.notificationservice.dto.NotificationRequest;
import tn.dwelink.notificationservice.dto.NotificationResponse;
import tn.dwelink.notificationservice.exception.AccesInterditException;
import tn.dwelink.notificationservice.exception.RessourceIntrouvableException;
import tn.dwelink.notificationservice.modele.Notification;
import tn.dwelink.notificationservice.repository.NotificationRepository;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // TEMPORAIRE : remplacé par la consommation RabbitMQ.
    public NotificationResponse creer(NotificationRequest req) {
        Notification notification = Notification.builder()
                .destinataireId(req.destinataireId())
                .type(req.type().trim())
                .titre(req.titre().trim())
                .message(req.message().trim())
                .build();
        return NotificationResponse.depuis(notificationRepository.save(notification));
    }

    public List<NotificationResponse> mesNotifications(Long utilisateurId, Boolean lue) {
        List<Notification> notifications = lue == null
                ? notificationRepository.findByDestinataireIdOrderByDateCreationDesc(utilisateurId)
                : notificationRepository.findByDestinataireIdAndLueOrderByDateCreationDesc(utilisateurId, lue);
        return notifications.stream().map(NotificationResponse::depuis).toList();
    }

    public long nombreNonLues(Long utilisateurId) {
        return notificationRepository.countByDestinataireIdAndLueFalse(utilisateurId);
    }

    public NotificationResponse marquerCommeLue(String id, Long utilisateurId) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Notification introuvable : " + id));
        if (!notification.getDestinataireId().equals(utilisateurId)) {
            throw new AccesInterditException("Cette notification ne vous appartient pas");
        }
        if (!notification.isLue()) {
            notification.marquerCommeLue();
            notification = notificationRepository.save(notification);
        }
        return NotificationResponse.depuis(notification);
    }

    public void toutMarquerCommeLu(Long utilisateurId) {
        List<Notification> nonLues = notificationRepository.findByDestinataireIdAndLueFalse(utilisateurId);
        nonLues.forEach(Notification::marquerCommeLue);
        notificationRepository.saveAll(nonLues);
    }
}
