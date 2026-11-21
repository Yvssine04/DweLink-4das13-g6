package com.dwelink.user.service.impl;

import com.dwelink.user.dto.request.*;
import com.dwelink.user.dto.response.*;
import com.dwelink.user.entity.*;
import com.dwelink.user.repository.DemandePharmacienRepository;
import com.dwelink.user.repository.JustificatifDemandeRepository;
import com.dwelink.user.repository.ProfilUtilisateurRepository;
import com.dwelink.user.security.jwt.JwtUtils;
import com.dwelink.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final ProfilUtilisateurRepository userRepository;
    private final DemandePharmacienRepository demandeRepository;
    private final JustificatifDemandeRepository justificatifRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public UserServiceImpl(ProfilUtilisateurRepository userRepository,
                           DemandePharmacienRepository demandeRepository,
                           JustificatifDemandeRepository justificatifRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.demandeRepository = demandeRepository;
        this.justificatifRepository = justificatifRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Un compte existe déjà avec cet email");
        }

        ProfilUtilisateur user = new ProfilUtilisateur();
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        user.setPrenom(request.getPrenom());
        user.setNom(request.getNom());
        user.setTelephone(request.getTelephone());

        RoleUtilisateur role = RoleUtilisateur.PATIENT;
        if (StringUtils.hasText(request.getRole())) {
            try {
                role = RoleUtilisateur.valueOf(request.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rôle invalide: " + request.getRole());
            }
        }
        user.setRole(role);

        if (role == RoleUtilisateur.PHARMACIEN) {
            user.setStatut(StatutCompte.EN_ATTENTE_VALIDATION);
        } else {
            user.setStatut(StatutCompte.ACTIF);
        }

        ProfilUtilisateur saved = userRepository.save(user);
        String token = jwtUtils.generateToken(saved.getId(), saved.getEmail(), saved.getRole().name());

        return new AuthResponse(token, saved.getId(), saved.getEmail(), saved.getPrenom(), saved.getNom(), saved.getRole().name(), saved.getStatut().name());
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        ProfilUtilisateur user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou mot de passe incorrect"));

        if (!passwordEncoder.matches(request.getMotDePasse(), user.getMotDePasse())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou mot de passe incorrect");
        }

        if (user.getStatut() == StatutCompte.SUSPENDU) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Votre compte a été suspendu");
        }
        if (user.getStatut() == StatutCompte.DESACTIVE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Votre compte a été désactivé");
        }

        String token = jwtUtils.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getPrenom(), user.getNom(), user.getRole().name(), user.getStatut().name());
    }

    @Override
    @Transactional(readOnly = true)
    public ProfilResponse getCurrentUserProfile(String email) {
        ProfilUtilisateur user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
        return mapToProfilResponse(user);
    }

    @Override
    public ProfilResponse updateCurrentUserProfile(String email, UpdateProfilRequest request) {
        ProfilUtilisateur user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        if (StringUtils.hasText(request.getPrenom())) user.setPrenom(request.getPrenom());
        if (StringUtils.hasText(request.getNom())) user.setNom(request.getNom());
        if (request.getTelephone() != null) user.setTelephone(request.getTelephone());
        if (StringUtils.hasText(request.getLangue())) user.setLangue(request.getLangue());
        if (request.getRayonRechercheKm() != null) user.setRayonRechercheKm(request.getRayonRechercheKm());

        ProfilUtilisateur updated = userRepository.save(user);
        return mapToProfilResponse(updated);
    }

    @Override
    public DemandePharmacienResponse submitDemandePharmacien(String email, DemandePharmacienRequest request) {
        ProfilUtilisateur user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        DemandePharmacien demande = new DemandePharmacien();
        demande.setUtilisateur(user);
        demande.setNumeroOrdre(request.getNumeroOrdre());
        demande.setDiplome(request.getDiplome());
        demande.setStatut(StatutDemande.DEPOSEE);

        DemandePharmacien saved = demandeRepository.save(demande);
        return mapToDemandeResponse(saved);
    }

    @Override
    public JustificatifResponse addJustificatifToDemande(Long demandeId, String email, JustificatifRequest request) {
        DemandePharmacien demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable"));

        if (!demande.getUtilisateur().getEmail().equalsIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Non autorisé à modifier cette demande");
        }

        TypeJustificatif type;
        try {
            type = TypeJustificatif.valueOf(request.getType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Type de justificatif invalide");
        }

        JustificatifDemande justificatif = new JustificatifDemande();
        justificatif.setDemande(demande);
        justificatif.setType(type);
        justificatif.setNomFichier(request.getNomFichier());

        JustificatifDemande saved = justificatifRepository.save(justificatif);
        return new JustificatifResponse(saved.getId(), saved.getType().name(), saved.getNomFichier(), saved.getDateAjout());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DemandePharmacienResponse> getDemandesPharmacien(String statut) {
        List<DemandePharmacien> list;
        if (StringUtils.hasText(statut)) {
            try {
                StatutDemande s = StatutDemande.valueOf(statut.toUpperCase());
                list = demandeRepository.findByStatut(s);
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Statut invalide: " + statut);
            }
        } else {
            list = demandeRepository.findAll();
        }
        return list.stream().map(this::mapToDemandeResponse).collect(Collectors.toList());
    }

    @Override
    public DemandePharmacienResponse decideDemande(Long demandeId, String adminEmail, DecisionDemandeRequest request) {
        ProfilUtilisateur admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Administrateur introuvable"));

        DemandePharmacien demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable"));

        if (demande.getStatut() != StatutDemande.DEPOSEE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cette demande a déjà été traitée");
        }

        StatutDemande decision;
        try {
            decision = StatutDemande.valueOf(request.getDecision().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Décision invalide: " + request.getDecision());
        }

        demande.setStatut(decision);
        demande.setExamineePar(admin.getId());
        demande.setCommentaireDecision(request.getCommentaire());
        demande.setDateDecision(LocalDateTime.now());

        if (decision == StatutDemande.APPROUVEE) {
            ProfilUtilisateur user = demande.getUtilisateur();
            user.setRole(RoleUtilisateur.PHARMACIEN);
            user.setStatut(StatutCompte.ACTIF);
            userRepository.save(user);
        }

        DemandePharmacien saved = demandeRepository.save(demande);
        return mapToDemandeResponse(saved);
    }

    @Override
    public ProfilResponse updateAccountStatus(Long userId, StatutCompteRequest request) {
        ProfilUtilisateur user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        try {
            StatutCompte nouveauStatut = StatutCompte.valueOf(request.getStatut().toUpperCase());
            user.setStatut(nouveauStatut);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Statut de compte invalide: " + request.getStatut());
        }

        ProfilUtilisateur saved = userRepository.save(user);
        return mapToProfilResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserInternalResponse getUserInternalById(Long userId) {
        ProfilUtilisateur user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
        return new UserInternalResponse(user.getId(), user.getEmail(), user.getPrenom(), user.getNom(), user.getRole().name(), user.getStatut().name());
    }

    @Override
    @Transactional(readOnly = true)
    public UserContactResponse getUserContactById(Long userId) {
        ProfilUtilisateur user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
        return new UserContactResponse(user.getId(), user.getEmail(), user.getPrenom(), user.getNom(), user.getTelephone());
    }

    private ProfilResponse mapToProfilResponse(ProfilUtilisateur user) {
        ProfilResponse res = new ProfilResponse();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setPrenom(user.getPrenom());
        res.setNom(user.getNom());
        res.setTelephone(user.getTelephone());
        res.setRole(user.getRole().name());
        res.setStatut(user.getStatut().name());
        res.setLangue(user.getLangue());
        res.setRayonRechercheKm(user.getRayonRechercheKm());
        res.setDateCreation(user.getDateCreation());
        return res;
    }

    private DemandePharmacienResponse mapToDemandeResponse(DemandePharmacien d) {
        DemandePharmacienResponse res = new DemandePharmacienResponse();
        res.setId(d.getId());
        res.setUtilisateurId(d.getUtilisateur().getId());
        res.setEmailUtilisateur(d.getUtilisateur().getEmail());
        res.setNomCompletUtilisateur(d.getUtilisateur().getPrenom() + " " + d.getUtilisateur().getNom());
        res.setNumeroOrdre(d.getNumeroOrdre());
        res.setDiplome(d.getDiplome());
        res.setStatut(d.getStatut().name());
        res.setDateDepot(d.getDateDepot());
        res.setDateDecision(d.getDateDecision());
        res.setExamineePar(d.getExamineePar());
        res.setCommentaireDecision(d.getCommentaireDecision());

        if (d.getJustificatifs() != null) {
            res.setJustificatifs(d.getJustificatifs().stream()
                    .map(j -> new JustificatifResponse(j.getId(), j.getType().name(), j.getNomFichier(), j.getDateAjout()))
                    .collect(Collectors.toList()));
        }
        return res;
    }
}
