package tn.dwelink.pharmacyservice.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.dwelink.pharmacyservice.dto.MembreResponse;
import tn.dwelink.pharmacyservice.dto.VerificationMembreResponse;
import tn.dwelink.pharmacyservice.entity.MembrePharmacie;
import tn.dwelink.pharmacyservice.entity.Pharmacie;
import tn.dwelink.pharmacyservice.enums.RoleMembre;
import tn.dwelink.pharmacyservice.enums.StatutMembre;
import tn.dwelink.pharmacyservice.enums.StatutPharmacie;
import tn.dwelink.pharmacyservice.evenement.DemandeRattachementCreee;
import tn.dwelink.pharmacyservice.evenement.MembresPharmacieModifies;
import tn.dwelink.pharmacyservice.evenement.PublicateurEvenements;
import tn.dwelink.pharmacyservice.evenement.TypeChangementMembre;
import tn.dwelink.pharmacyservice.exception.ConflitException;
import tn.dwelink.pharmacyservice.exception.RequeteInvalideException;
import tn.dwelink.pharmacyservice.exception.RessourceIntrouvableException;
import tn.dwelink.pharmacyservice.repository.MembrePharmacieRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class MembreService {

    private final PharmacieService pharmacieService;
    private final MembrePharmacieRepository membreRepository;
    private final PublicateurEvenements publicateurEvenements;

    public MembreResponse demander(Long pharmacieId, Long utilisateurId) {
        Pharmacie pharmacie = pharmacieService.charger(pharmacieId);
        MembrePharmacie existant = membreRepository.findByPharmacieIdAndUtilisateurId(pharmacieId, utilisateurId)
                .orElse(null);
        if (existant != null) {
            if (existant.getStatut() != StatutMembre.REFUSE) {
                throw new ConflitException("Cet utilisateur est déjà membre de cette pharmacie (ou a déjà fait une demande)");
            }
            existant.setStatut(StatutMembre.EN_ATTENTE);
            existant.setRole(RoleMembre.ASSISTANT);
            publierDemande(pharmacie, existant);
            return MembreResponse.from(existant);
        }
        MembrePharmacie membre = MembrePharmacie.builder()
                .utilisateurId(utilisateurId)
                .role(RoleMembre.ASSISTANT)
                .statut(StatutMembre.EN_ATTENTE)
                .pharmacie(pharmacie)
                .build();
        pharmacie.getMembres().add(membre);
        MembrePharmacie enregistre = membreRepository.save(membre);
        publierDemande(pharmacie, enregistre);
        return MembreResponse.from(enregistre);
    }

    public MembreResponse decider(Long pharmacieId, Long membreId, StatutMembre decision, Long appelantId) {
        if (decision != StatutMembre.ACTIF && decision != StatutMembre.REFUSE) {
            throw new RequeteInvalideException("La décision doit être ACTIF ou REFUSE");
        }
        pharmacieService.charger(pharmacieId);
        pharmacieService.verifierTitulaire(pharmacieId, appelantId);
        MembrePharmacie membre = chargerMembre(pharmacieId, membreId);
        if (membre.getStatut() != StatutMembre.EN_ATTENTE) {
            throw new ConflitException("Cette demande a déjà été traitée (statut actuel : " + membre.getStatut() + ")");
        }
        StatutMembre ancienStatut = membre.getStatut();
        membre.setStatut(decision);
        if (ancienStatut != decision) {
            TypeChangementMembre changement = decision == StatutMembre.ACTIF
                    ? TypeChangementMembre.ACCEPTE
                    : TypeChangementMembre.REFUSE;
            publierChangement(pharmacieId, membre, changement);
        }
        return MembreResponse.from(membre);
    }

    public void retirer(Long pharmacieId, Long membreId, Long appelantId) {
        Pharmacie pharmacie = pharmacieService.charger(pharmacieId);
        pharmacieService.verifierTitulaire(pharmacieId, appelantId);
        MembrePharmacie membre = chargerMembre(pharmacieId, membreId);
        if (membre.getRole() == RoleMembre.TITULAIRE) {
            throw new ConflitException("Impossible de retirer le titulaire : une pharmacie doit toujours en avoir un");
        }
        pharmacie.getMembres().remove(membre);
        publierChangement(pharmacieId, membre, TypeChangementMembre.RETIRE);
    }

    /**
     * Compte suspendu dans user-service : l'utilisateur perd ses rattachements (hors titulaire)
     * et les pharmacies dont il est titulaire passent en SUSPENDUE.
     */
    public void suspendreUtilisateur(Long utilisateurId) {
        List<MembrePharmacie> lignes = membreRepository.findByUtilisateurId(utilisateurId);
        for (MembrePharmacie membre : lignes) {
            Pharmacie pharmacie = membre.getPharmacie();
            if (membre.getRole() == RoleMembre.TITULAIRE) {
                pharmacieService.changerStatut(pharmacie.getId(), StatutPharmacie.SUSPENDUE);
            } else {
                pharmacie.getMembres().remove(membre);
                publierChangement(pharmacie.getId(), membre, TypeChangementMembre.RETIRE);
            }
        }
    }

    @Transactional(readOnly = true)
    public VerificationMembreResponse verifier(Long pharmacieId, Long utilisateurId) {
        pharmacieService.charger(pharmacieId);
        return membreRepository.findByPharmacieIdAndUtilisateurId(pharmacieId, utilisateurId)
                .filter(m -> m.getStatut() == StatutMembre.ACTIF)
                .map(m -> new VerificationMembreResponse(true, m.getRole()))
                .orElse(new VerificationMembreResponse(false, null));
    }

    @Transactional(readOnly = true)
    public List<MembreResponse> lister(Long pharmacieId, Long appelantId) {
        Pharmacie pharmacie = pharmacieService.charger(pharmacieId);
        pharmacieService.verifierTitulaire(pharmacieId, appelantId);
        return pharmacie.getMembres().stream().map(MembreResponse::from).toList();
    }

    private void publierDemande(Pharmacie pharmacie, MembrePharmacie membre) {
        publicateurEvenements.publierDemandeRattachementCreee(new DemandeRattachementCreee(
                pharmacie.getId(), pharmacie.getNom(), membre.getId(), membre.getUtilisateurId(), LocalDateTime.now()));
    }

    private void publierChangement(Long pharmacieId, MembrePharmacie membre, TypeChangementMembre changement) {
        publicateurEvenements.publierMembresPharmacieModifies(new MembresPharmacieModifies(
                pharmacieId, membre.getId(), membre.getUtilisateurId(), changement, LocalDateTime.now()));
    }

    private MembrePharmacie chargerMembre(Long pharmacieId, Long membreId) {
        return membreRepository.findById(membreId)
                .filter(m -> m.getPharmacie().getId().equals(pharmacieId))
                .orElseThrow(() -> new RessourceIntrouvableException(
                        "Membre " + membreId + " introuvable dans la pharmacie " + pharmacieId));
    }
}
