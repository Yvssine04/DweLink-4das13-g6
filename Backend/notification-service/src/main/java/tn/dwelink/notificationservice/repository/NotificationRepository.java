package tn.dwelink.notificationservice.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import tn.dwelink.notificationservice.modele.Notification;

public interface NotificationRepository extends MongoRepository<Notification, String> {

    List<Notification> findByDestinataireIdOrderByDateCreationDesc(Long destinataireId);

    List<Notification> findByDestinataireIdAndLueOrderByDateCreationDesc(Long destinataireId, boolean lue);

    List<Notification> findByDestinataireIdAndLueFalse(Long destinataireId);

    long countByDestinataireIdAndLueFalse(Long destinataireId);
}
