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

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "company_name", nullable = false, unique = true)
    private String companyName;

    // Relación Uno a Muchos con las credenciales WebAuthn
    @OneToMany(mappedBy = "lessor", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude // Evita problemas de recursión infinita si haces un log de la entidad
    private List<PasskeyCredentialEntity> credentials = new ArrayList<>();

    // Método de conveniencia para mantener la sincronización bidireccional
    public void addCredential(PasskeyCredentialEntity credential) {
        credentials.add(credential);
        credential.setLessor(this);
    }

    public void removeCredential(PasskeyCredentialEntity credential) {
        credentials.remove(credential);
        credential.setLessor(null);
    }
}
