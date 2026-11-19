package tn.dwelink.pharmacyservice.service;

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

    public MembreResponse demander(Long pharmacieId, Long utilisateurId) {
        Pharmacie pharmacie = pharmacieService.charger(pharmacieId);
        if (membreRepository.findByPharmacieIdAndUtilisateurId(pharmacieId, utilisateurId).isPresent()) {
            throw new ConflitException("Cet utilisateur est déjà membre de cette pharmacie (ou a déjà fait une demande)");
        }
        MembrePharmacie membre = MembrePharmacie.builder()
                .utilisateurId(utilisateurId)
                .role(RoleMembre.ASSISTANT)
                .statut(StatutMembre.EN_ATTENTE)
                .pharmacie(pharmacie)
                .build();
        pharmacie.getMembres().add(membre);
        return MembreResponse.from(membreRepository.save(membre));
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
        membre.setStatut(decision);
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

    private MembrePharmacie chargerMembre(Long pharmacieId, Long membreId) {
        return membreRepository.findById(membreId)
                .filter(m -> m.getPharmacie().getId().equals(pharmacieId))
                .orElseThrow(() -> new RessourceIntrouvableException(
                        "Membre " + membreId + " introuvable dans la pharmacie " + pharmacieId));
    }
}
