package tn.dwelink.pharmacyservice.evenement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import tn.dwelink.pharmacyservice.config.RabbitConfig;

@Slf4j
@Component
@RequiredArgsConstructor
public class PublicateurEvenements {

    public static final String CLE_STATUT_PHARMACIE_MODIFIE = "pharmacie.statut.modifie";
    public static final String CLE_RATTACHEMENT_CREE = "pharmacie.rattachement.cree";
    public static final String CLE_MEMBRES_MODIFIES = "pharmacie.membres.modifies";

    private final RabbitTemplate rabbitTemplate;

    public void publierStatutPharmacieModifie(StatutPharmacieModifie evenement) {
        publierApresCommit(CLE_STATUT_PHARMACIE_MODIFIE, evenement);
    }

    public void publierDemandeRattachementCreee(DemandeRattachementCreee evenement) {
        publierApresCommit(CLE_RATTACHEMENT_CREE, evenement);
    }

    public void publierMembresPharmacieModifies(MembresPharmacieModifies evenement) {
        publierApresCommit(CLE_MEMBRES_MODIFIES, evenement);
    }

    /**
     * Si une transaction est en cours, l'envoi a lieu après son commit,
     * pour ne jamais annoncer un changement qui n'a pas été enregistré.
     */
    private void publierApresCommit(String cleRoutage, Object evenement) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    envoyer(cleRoutage, evenement);
                }
            });
        } else {
            envoyer(cleRoutage, evenement);
        }
    }

    private void envoyer(String cleRoutage, Object evenement) {
        try {
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_EVENEMENTS, cleRoutage, evenement);
            log.info("Événement publié sur {} ({}) : {}", RabbitConfig.EXCHANGE_EVENEMENTS, cleRoutage, evenement);
        } catch (AmqpException e) {
            log.warn("Échec de publication de l'événement {} sur {} : {}",
                    cleRoutage, RabbitConfig.EXCHANGE_EVENEMENTS, e.getMessage());
        }
    }
}
