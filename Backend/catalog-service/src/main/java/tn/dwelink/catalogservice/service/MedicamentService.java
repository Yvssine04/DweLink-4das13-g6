package tn.dwelink.catalogservice.service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import tn.dwelink.catalogservice.dto.*;
import tn.dwelink.catalogservice.entity.CompositionMedicament;
import tn.dwelink.catalogservice.entity.Medicament;
import tn.dwelink.catalogservice.entity.PrincipeActif;
import tn.dwelink.catalogservice.enums.StatutMedicament;
import tn.dwelink.catalogservice.repository.MedicamentRepository;
import tn.dwelink.catalogservice.repository.PrincipeActifRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class MedicamentService implements IMedicamentService {

    private final MedicamentRepository medicamentRepository;
    private final PrincipeActifRepository principeActifRepository;
    @Value("${app.upload-dir}")
    private String uploadDir;

    @Override
    @Transactional
    public MedicamentResponse creer(MedicamentRequest request) {
        String code = request.codeBarres().trim();
        if (medicamentRepository.existsByCodeBarres(code)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Code-barres déjà utilisé");
        }

        if (request.compositions() == null || request.compositions().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Au moins une composition est obligatoire");
        }

        Medicament medicament = new Medicament();
        medicament.setNomCommercial(request.nomCommercial());
        medicament.setCodeBarres(code);
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

            for (CompositionMedicament deja : medicament.getCompositions()) {
                if (deja.getPrincipeActif().getId().equals(principeActif.getId())) {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT, "Principe actif en double dans la composition");
                }
            }

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
        Medicament medicament = trouver(id);
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
                m.getStatut(), m.getDateRetrait(), m.getCategorie(), m.getLaboratoire(),m.getImageUrl(),
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

    @Override
    @Transactional
    public MedicamentResponse modifier(Long id, MedicamentUpdateRequest request) {

        Medicament medicament = trouver(id);

        if (medicament.getStatut() == StatutMedicament.RETIRE) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Un médicament retiré ne peut plus être modifié");
        }

        if (medicamentRepository.existsByCodeBarresAndIdNot(
                request.codeBarres(), id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Code-barres déjà utilisé"
            );
        }

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
            medicament.setConditionConservation(
                    request.conditionConservation()
            );
        }

        return versResponse(medicament);
    }

    @Override
    @Transactional
    public MedicamentResponse ajouterComposition(Long id, CompositionRequest request) {
        Medicament medicament = trouver(id);

        PrincipeActif principeActif = principeActifRepository.findById(request.principeActifId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Principe actif introuvable"));


        for (CompositionMedicament c : medicament.getCompositions()) {
            if (c.getPrincipeActif().getId().equals(principeActif.getId())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "Ce principe actif est déjà dans la composition du médicament");
            }
        }

        CompositionMedicament composition = new CompositionMedicament();
        composition.setPrincipeActif(principeActif);
        composition.setDosage(request.dosage());
        composition.setUnite(request.unite());
        composition.setMedicament(medicament);
        medicament.getCompositions().add(composition);

        medicamentRepository.saveAndFlush(medicament);
        return versResponse(medicament);
    }

    @Override
    @Transactional
    public void supprimerComposition(Long id, Long compositionId) {
        Medicament medicament = trouver(id);

        CompositionMedicament aSupprimer = null;
        for (CompositionMedicament c : medicament.getCompositions()) {
            if (c.getId().equals(compositionId)) {
                aSupprimer = c;
            }
        }
        if (aSupprimer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Composition introuvable");
        }
        if (medicament.getCompositions().size() == 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Impossible de supprimer la dernière composition");
        }
        medicament.getCompositions().remove(aSupprimer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicamentResponse> equivalents(Long id) {
        Medicament origine = trouver(id);
        List<MedicamentResponse> resultat = new ArrayList<>();

        List<Medicament> candidats = medicamentRepository.findByFormeAndStatutAndIdNot(
                origine.getForme(), StatutMedicament.ACTIF, id);

        for (Medicament candidat : candidats) {
            if (memeComposition(origine, candidat)) {
                resultat.add(versResponse(candidat));
            }
        }
        return resultat;
    }

    private boolean memeComposition(Medicament a, Medicament b) {
        if (a.getCompositions().size() != b.getCompositions().size()) {
            return false;
        }
        for (CompositionMedicament ca : a.getCompositions()) {
            boolean trouve = false;
            for (CompositionMedicament cb : b.getCompositions()) {
                if (ca.getPrincipeActif().getId().equals(cb.getPrincipeActif().getId())
                        && ca.getDosage().equals(cb.getDosage())
                        && ca.getUnite().equalsIgnoreCase(cb.getUnite())) {
                    trouve = true;
                }
            }
            if (!trouve) {
                return false;
            }
        }
        return true;
    }

    @Override
    @Transactional
    public MedicamentResponse retirer(Long id) {
        Medicament medicament = trouver(id);

        if (medicament.getStatut() == StatutMedicament.RETIRE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Médicament déjà retiré");
        }
        medicament.setStatut(StatutMedicament.RETIRE);
        medicament.setDateRetrait(LocalDate.now());

        // TODO  : publier l'événement RabbitMQ "MedicamentRetire"
        return versResponse(medicament);
    }

    @Override
    @Transactional
    public MedicamentResponse ajouterImage(Long id, MultipartFile file) {
        Medicament medicament = trouver(id);

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fichier vide");
        }

        String type = file.getContentType();
        String extension;
        if ("image/jpeg".equals(type)) {
            extension = ".jpg";
        } else if ("image/png".equals(type)) {
            extension = ".png";
        } else if ("image/webp".equals(type)) {
            extension = ".webp";
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Image JPG, PNG ou WEBP uniquement");
        }

        String ancienne = medicament.getImageUrl();
        String nomFichier = "medicament-" + id + "-" + System.currentTimeMillis() + extension;


        try {
            Path dossier = Path.of(uploadDir);
            Files.createDirectories(dossier);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, dossier.resolve(nomFichier));
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Échec de l'enregistrement de l'image");
        }

        medicament.setImageUrl(nomFichier);

        // Supprimer l'ancienne image
        if (ancienne != null) {
            try {
                Files.deleteIfExists(Path.of(uploadDir).resolve(ancienne));
            } catch (IOException e) {

            }
        }

        return versResponse(medicament);

    }

    @Override
    @Transactional
    public void supprimerImage(Long id) {
        Medicament medicament = trouver(id);
        if (medicament.getImageUrl() != null) {
            try {
                Files.deleteIfExists(Path.of(uploadDir).resolve(medicament.getImageUrl()));
            } catch (IOException e) {
                // ignoré
            }
            medicament.setImageUrl(null);
        }
    }

    private Medicament trouver(Long id) {
        return medicamentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Médicament introuvable"));
    }


}
