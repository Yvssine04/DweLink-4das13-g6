package tn.dwelink.notificationservice.modele;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreferenceNotification {

    @Id
    private String id;

    @Indexed(unique = true)
    private Long utilisateurId;

    @Builder.Default
    private boolean applicationActivee = true;

    @Builder.Default
    private boolean emailActive = true;
}
