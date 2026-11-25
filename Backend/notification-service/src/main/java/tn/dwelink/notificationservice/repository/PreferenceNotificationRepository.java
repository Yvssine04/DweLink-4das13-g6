package tn.dwelink.notificationservice.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import tn.dwelink.notificationservice.modele.PreferenceNotification;

public interface PreferenceNotificationRepository extends MongoRepository<PreferenceNotification, String> {

    Optional<PreferenceNotification> findByUtilisateurId(Long utilisateurId);
}
