package tn.dwelink.pharmacyservice.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException e) {
        Map<String, String> champs = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(err -> champs.putIfAbsent(err.getField(), err.getDefaultMessage()));
        return reponse(HttpStatus.BAD_REQUEST, "Données invalides", champs);
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
