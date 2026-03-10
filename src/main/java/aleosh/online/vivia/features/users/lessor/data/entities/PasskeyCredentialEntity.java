package aleosh.online.vivia.features.users.lessor.data.entities;

import aleosh.online.vivia.features.users.lessee.data.entities.LesseeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Passkey_Credentials")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasskeyCredentialEntity {

    // El ID de la credencial es único, se usa como llave primaria
    @Id
    @Column(name = "credential_id", nullable = false, updatable = false)
    private byte[] credentialId;

    // Almacenamos la llave pública en formato de bytes.
    // Usamos @Lob porque dependiendo del algoritmo, la llave puede exceder el límite de un VARCHAR estándar.
    @Lob
    @Column(name = "public_key", nullable = false)
    private byte[] publicKey;

    @Column(name = "sign_count", nullable = false)
    private long signCount;

    // Relación Muchos a Uno con el Arrendador
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lessor_id", nullable = true)
    private LessorEntity lessor;

    // Relación Muchos a Uno con el Arrendatario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lessee_id", nullable = true)
    private LesseeEntity lessee;
}