package tn.dwelink.notificationservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.dwelink.notificationservice.dto.PreferenceRequest;
import tn.dwelink.notificationservice.dto.PreferenceResponse;
import tn.dwelink.notificationservice.modele.PreferenceNotification;
import tn.dwelink.notificationservice.repository.PreferenceNotificationRepository;

@Service
@RequiredArgsConstructor
public class PreferenceNotificationService {

    private final PreferenceNotificationRepository preferenceRepository;

    /** Renvoie les préférences enregistrées, ou les valeurs par défaut (sans les enregistrer). */
    public PreferenceResponse consulter(Long utilisateurId) {
        return PreferenceResponse.depuis(trouverOuDefaut(utilisateurId));
    }

    /** Met à jour les préférences, en les créant si elles n'existent pas encore. */
    public PreferenceResponse modifier(Long utilisateurId, PreferenceRequest req) {
        PreferenceNotification preference = trouverOuDefaut(utilisateurId);
        preference.setApplicationActivee(req.applicationActivee());
        preference.setEmailActive(req.emailActive());
        return PreferenceResponse.depuis(preferenceRepository.save(preference));
    }

    private PreferenceNotification trouverOuDefaut(Long utilisateurId) {
        return preferenceRepository.findByUtilisateurId(utilisateurId)
                .orElseGet(() -> PreferenceNotification.builder().utilisateurId(utilisateurId).build());
    }
}
