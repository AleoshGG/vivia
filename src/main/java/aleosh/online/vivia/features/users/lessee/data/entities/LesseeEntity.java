package aleosh.online.vivia.features.users.lessee.data.entities;

import aleosh.online.vivia.features.users.lessor.data.entities.PasskeyCredentialEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Lessees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LesseeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    //@Lob
    @Column(name = "user_handle", nullable = false, unique = true)
    private byte[] userHandle;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @OneToMany(mappedBy = "lessee", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<PasskeyCredentialEntity> credentials = new ArrayList<>();

    public void addCredential(PasskeyCredentialEntity credential) {
        credentials.add(credential);
        credential.setLessee(this);
    }

    public void removeCredential(PasskeyCredentialEntity credential) {
        credentials.remove(credential);
        credential.setLessee(null);
    }
}