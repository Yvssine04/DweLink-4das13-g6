package tn.dwelink.pharmacyservice.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GestionErreurs {

    @ExceptionHandler(RessourceIntrouvableException.class)
    public ResponseEntity<Map<String, Object>> introuvable(RessourceIntrouvableException e) {
        return reponse(HttpStatus.NOT_FOUND, e.getMessage(), null);
    }

    @ExceptionHandler(ConflitException.class)
    public ResponseEntity<Map<String, Object>> conflit(ConflitException e) {
        return reponse(HttpStatus.CONFLICT, e.getMessage(), null);
    }

    @ExceptionHandler(AccesInterditException.class)
    public ResponseEntity<Map<String, Object>> accesInterdit(AccesInterditException e) {
        return reponse(HttpStatus.FORBIDDEN, e.getMessage(), null);
    }

    @ExceptionHandler(RequeteInvalideException.class)
    public ResponseEntity<Map<String, Object>> requeteInvalide(RequeteInvalideException e) {
        return reponse(HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException e) {
        Map<String, String> champs = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(err -> champs.putIfAbsent(err.getField(), err.getDefaultMessage()));
        return reponse(HttpStatus.BAD_REQUEST, "Données invalides", champs);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, Object>> validationListe(HandlerMethodValidationException e) {
        Map<String, String> champs = new LinkedHashMap<>();
        e.getParameterValidationResults().forEach(resultat -> {
            String prefixe = resultat.getContainerIndex() != null ? "[" + resultat.getContainerIndex() + "]." : "";
            resultat.getResolvableErrors().forEach(err -> {
                String champ = err instanceof FieldError fe
                        ? prefixe + fe.getField()
                        : resultat.getMethodParameter().getParameterName();
                champs.putIfAbsent(champ, err.getDefaultMessage());
            });
        });
        return reponse(HttpStatus.BAD_REQUEST, "Données invalides", champs);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Map<String, Object>> enteteManquant(MissingRequestHeaderException e) {
        return reponse(HttpStatus.BAD_REQUEST, "En-tête obligatoire manquant : " + e.getHeaderName(), null);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> parametreManquant(MissingServletRequestParameterException e) {
        return reponse(HttpStatus.BAD_REQUEST, "Paramètre obligatoire manquant : " + e.getParameterName(), null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> typeInvalide(MethodArgumentTypeMismatchException e) {
        return reponse(HttpStatus.BAD_REQUEST, "Valeur invalide pour : " + e.getName(), null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> jsonIllisible(HttpMessageNotReadableException e) {
        return reponse(HttpStatus.BAD_REQUEST,
                "Corps de la requête illisible (JSON mal formé ou valeur inconnue)", null);
    }

    private ResponseEntity<Map<String, Object>> reponse(HttpStatus statut, String message, Map<String, String> champs) {
        Map<String, Object> corps = new LinkedHashMap<>();
        corps.put("code", statut.value());
        corps.put("message", message);
        corps.put("date", LocalDateTime.now());
        if (champs != null) {
            corps.put("champs", champs);
        }
        return ResponseEntity.status(statut).body(corps);
    }
}
