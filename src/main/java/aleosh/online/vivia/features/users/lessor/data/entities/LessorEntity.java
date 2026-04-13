package aleosh.online.vivia.features.users.lessor.data.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Lessors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // NUEVO CAMPO OBLIGATORIO PARA WEBAUTHN
    //@Lob
    @Column(name = "user_handle", nullable = false, unique = true)
    private byte[] userHandle;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "company_name", nullable = false, unique = true)
    private String companyName;

    @Column(name = "password")
    private String password;

    @Column(name = "phone_number", nullable = false, length = 11)
    private String phoneNumber;

    @OneToMany(mappedBy = "lessor", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<PasskeyCredentialEntity> credentials = new ArrayList<>();

    public void addCredential(PasskeyCredentialEntity credential) {
        credentials.add(credential);
        credential.setLessor(this);
    }

    public void removeCredential(PasskeyCredentialEntity credential) {
        credentials.remove(credential);
        credential.setLessor(null);
    }
}