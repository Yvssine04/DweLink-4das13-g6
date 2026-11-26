package tn.dwelink.notificationservice.modele;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import tn.dwelink.notificationservice.enums.StatutEnvoi;

@Document(collection = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    private String id;

    @Indexed
    private Long destinataireId;

    private String type;

    private String titre;

    private String message;

    @Builder.Default
    private boolean lue = false;

    @Builder.Default
    private StatutEnvoi statutEnvoi = StatutEnvoi.EN_ATTENTE;

    @Builder.Default
    private LocalDateTime dateCreation = LocalDateTime.now();

    public void marquerCommeLue() {
        this.lue = true;
    }
}
