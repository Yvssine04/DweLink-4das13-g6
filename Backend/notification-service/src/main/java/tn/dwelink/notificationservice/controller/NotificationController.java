package tn.dwelink.notificationservice.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tn.dwelink.notificationservice.dto.NombreNonLuesResponse;
import tn.dwelink.notificationservice.dto.NotificationRequest;
import tn.dwelink.notificationservice.dto.NotificationResponse;
import tn.dwelink.notificationservice.dto.PreferenceRequest;
import tn.dwelink.notificationservice.dto.PreferenceResponse;
import tn.dwelink.notificationservice.service.NotificationService;
import tn.dwelink.notificationservice.service.PreferenceNotificationService;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final PreferenceNotificationService preferenceService;

    // TEMPORAIRE : remplacé par la consommation RabbitMQ.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationResponse creer(@Valid @RequestBody NotificationRequest req) {
        return notificationService.creer(req);
    }

    @GetMapping
    public List<NotificationResponse> mesNotifications(@RequestHeader("X-Utilisateur-Id") Long utilisateurId,
                                                       @RequestParam(required = false) Boolean lue) {
        return notificationService.mesNotifications(utilisateurId, lue);
    }

    @GetMapping("/non-lues/nombre")
    public NombreNonLuesResponse nombreNonLues(@RequestHeader("X-Utilisateur-Id") Long utilisateurId) {
        return new NombreNonLuesResponse(notificationService.nombreNonLues(utilisateurId));
    }

    @PutMapping("/tout-lu")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void toutMarquerCommeLu(@RequestHeader("X-Utilisateur-Id") Long utilisateurId) {
        notificationService.toutMarquerCommeLu(utilisateurId);
    }

    @GetMapping("/preferences")
    public PreferenceResponse mesPreferences(@RequestHeader("X-Utilisateur-Id") Long utilisateurId) {
        return preferenceService.consulter(utilisateurId);
    }

    @PutMapping("/preferences")
    public PreferenceResponse modifierPreferences(@RequestHeader("X-Utilisateur-Id") Long utilisateurId,
                                                  @Valid @RequestBody PreferenceRequest req) {
        return preferenceService.modifier(utilisateurId, req);
    }

    @PutMapping("/{id}/lue")
    public NotificationResponse marquerCommeLue(@PathVariable String id,
                                                @RequestHeader("X-Utilisateur-Id") Long utilisateurId) {
        return notificationService.marquerCommeLue(id, utilisateurId);
    }
}
