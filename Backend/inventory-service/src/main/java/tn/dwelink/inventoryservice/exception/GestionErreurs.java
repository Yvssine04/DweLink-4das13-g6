package tn.dwelink.inventoryservice.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
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

    @ExceptionHandler(RequeteInvalideException.class)
    public ResponseEntity<Map<String, Object>> invalide(RequeteInvalideException e) {
        return reponse(HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }

    // Levée par MongoDB quand l'index unique pharmacie + médicament est violé
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Map<String, Object>> doublon(DuplicateKeyException e) {
        return reponse(HttpStatus.CONFLICT, "Ce médicament est déjà dans le stock de cette pharmacie", null);
    }

    // Levée grâce à @Version quand deux requêtes modifient le même article en même temps
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<Map<String, Object>> concurrence(OptimisticLockingFailureException e) {
        return reponse(HttpStatus.CONFLICT, "Le stock a été modifié en même temps, réessayez", null);
    }

    // Levée par @Valid quand le JSON ne respecte pas @NotNull, @Min...
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