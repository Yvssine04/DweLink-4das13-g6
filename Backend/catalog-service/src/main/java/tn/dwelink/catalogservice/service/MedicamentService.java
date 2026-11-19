package tn.dwelink.catalogservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tn.dwelink.catalogservice.dto.CompositionRequest;
import tn.dwelink.catalogservice.dto.CompositionResponse;
import tn.dwelink.catalogservice.dto.MedicamentRequest;
import tn.dwelink.catalogservice.dto.MedicamentResponse;
import tn.dwelink.catalogservice.entity.CompositionMedicament;
import tn.dwelink.catalogservice.entity.Medicament;
import tn.dwelink.catalogservice.entity.PrincipeActif;
import tn.dwelink.catalogservice.repository.MedicamentRepository;
import tn.dwelink.catalogservice.repository.PrincipeActifRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicamentService implements IMedicamentService {

    private final MedicamentRepository medicamentRepository;
    private final PrincipeActifRepository principeActifRepository;

    @Override
    public MedicamentResponse creer(MedicamentRequest request) {

        if (medicamentRepository.existsByCodeBarres(request.codeBarres())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Code-barres déjà utilisé");
        }

        Medicament medicament = new Medicament();
        medicament.setNomCommercial(request.nomCommercial());
        medicament.setCodeBarres(request.codeBarres());
        medicament.setForme(request.forme());
        medicament.setCategorie(request.categorie());
        medicament.setLaboratoire(request.laboratoire());

        if (request.ordonnanceRequise() != null) {
            medicament.setOrdonnanceRequise(request.ordonnanceRequise());
        }
        if (request.stupefiant() != null) {
            medicament.setStupefiant(request.stupefiant());
        }
        if (request.conditionConservation() != null) {
            medicament.setConditionConservation(request.conditionConservation());
        }


        for (CompositionRequest cr : request.compositions()) {
            PrincipeActif principeActif = principeActifRepository.findById(cr.principeActifId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Principe actif introuvable"));

            CompositionMedicament composition = new CompositionMedicament();
            composition.setPrincipeActif(principeActif);
            composition.setDosage(cr.dosage());
            composition.setUnite(cr.unite());
            composition.setMedicament(medicament);

            medicament.getCompositions().add(composition);
        }

        Medicament sauvegarde = medicamentRepository.save(medicament);
        return versResponse(sauvegarde);
    }

    @Override
    @Transactional(readOnly = true)
    public MedicamentResponse consulter(Long id) {
        Medicament medicament = medicamentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Médicament introuvable"));
        return versResponse(medicament);
    }


    private MedicamentResponse versResponse(Medicament m) {
        List<CompositionResponse> compositions = new ArrayList<>();

        for (CompositionMedicament c : m.getCompositions()) {
            compositions.add(new CompositionResponse(
                    c.getId(),
                    c.getPrincipeActif().getId(),
                    c.getPrincipeActif().getNomDci(),
                    c.getDosage(),
                    c.getUnite()));
        }

        return new MedicamentResponse(
                m.getId(), m.getNomCommercial(), m.getCodeBarres(), m.getForme(),
                m.getOrdonnanceRequise(), m.getConditionConservation(), m.getStupefiant(),
                m.getStatut(), m.getDateRetrait(), m.getCategorie(), m.getLaboratoire(),
                compositions);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicamentResponse> rechercher(String q, String categorie, String laboratoire) {
        List<Medicament> medicaments = medicamentRepository.findAll(Sort.by("nomCommercial"));
        List<MedicamentResponse> resultat = new ArrayList<>();

        for (Medicament m : medicaments) {
            boolean ok = true;

            if (q != null && !q.isBlank()
                    && !m.getNomCommercial().toLowerCase().contains(q.trim().toLowerCase())) {
                ok = false;
            }
            if (categorie != null && !categorie.isBlank()
                    && (m.getCategorie() == null
                    || !m.getCategorie().equalsIgnoreCase(categorie.trim()))) {
                ok = false;
            }
            if (laboratoire != null && !laboratoire.isBlank()
                    && (m.getLaboratoire() == null
                    || !m.getLaboratoire().equalsIgnoreCase(laboratoire.trim()))) {
                ok = false;
            }

            if (ok) {
                resultat.add(versResponse(m));
            }
        }
        return resultat;
    }
}
