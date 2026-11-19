package tn.dwelink.pharmacyservice.evenement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.stereotype.Component;
import tn.dwelink.pharmacyservice.config.RabbitConfig;
import tn.dwelink.pharmacyservice.service.MembreService;

@Slf4j
@Component
@RequiredArgsConstructor
public class EcouteurEvenements {

    public static final String STATUT_SUSPENDU = "SUSPENDU";

    private final MembreService membreService;

    @RabbitListener(queues = RabbitConfig.QUEUE_COMPTE, errorHandler = "gestionnaireErreursEcoute")
    public void surStatutCompteModifie(StatutCompteModifie evenement) {
        if (evenement == null || evenement.utilisateurId() == null || evenement.nouveauStatut() == null) {
            throw new MessageConversionException("Événement StatutCompteModifie invalide : " + evenement);
        }
        if (STATUT_SUSPENDU.equals(evenement.nouveauStatut())) {
            log.info("Compte {} suspendu : retrait de ses rattachements aux pharmacies", evenement.utilisateurId());
            membreService.suspendreUtilisateur(evenement.utilisateurId());
        } else {
            log.info("Statut du compte {} passé à {} : aucune action côté pharmacie",
                    evenement.utilisateurId(), evenement.nouveauStatut());
        }
    }
}
