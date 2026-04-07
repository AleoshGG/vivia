package aleosh.online.vivia.features.users.lessor.data.mappers;

import aleosh.online.vivia.features.users.lessor.data.entities.LessorEntity;
import aleosh.online.vivia.features.users.lessor.data.entities.PasskeyCredentialEntity;
import aleosh.online.vivia.features.users.lessor.domain.entities.Lessor;
import aleosh.online.vivia.features.users.lessor.domain.valueobjects.PasskeyCredential;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component("lessorDataMapper")
public class LessorMapper {
    public Lessor toDomain(LessorEntity lessorEntity) {
        if (lessorEntity == null) { return null; }

        Lessor.Builder builder = Lessor.builder()
                .id(lessorEntity.getId())
                .userHandle(lessorEntity.getUserHandle()) // Agregado
                .firstName(lessorEntity.getFirstName())
                .lastName(lessorEntity.getLastName())
                .companyName(lessorEntity.getCompanyName())
                .password(lessorEntity.getPassword())
                .phoneNumber(lessorEntity.getPhoneNumber());

        // Iterar y mapear cada credencial de Entidad a Dominio
        if (lessorEntity.getCredentials() != null) {
            for (PasskeyCredentialEntity credEntity : lessorEntity.getCredentials()) {
                builder.addCredential(mapToDomainCredential(credEntity));
            }
        }

        return  builder.build();
    }

    public LessorEntity toEntity(Lessor lessor) {
        if (lessor == null) { return null; }

        LessorEntity lessorEntity = new LessorEntity();
        lessorEntity.setId(lessor.getId());
        lessorEntity.setUserHandle(lessor.getUserHandle());
        lessorEntity.setFirstName(lessor.getFirstName());
        lessorEntity.setLastName(lessor.getLastName());
        lessorEntity.setCompanyName(lessor.getCompanyName());
        lessorEntity.setPassword(lessor.getPassword());
        lessorEntity.setPhoneNumber(lessor.getPhoneNumber());

        // Iterar y mapear cada credencial de Dominio a Entidad
        if (lessor.getCredentials() != null) {
            for (PasskeyCredential cred : lessor.getCredentials()) {
                lessorEntity.addCredential(mapToEntityCredential(cred));
            }
        }

        return lessorEntity;
    }

    // Métodos auxiliares para la conversión de credenciales y Base64Url
    private PasskeyCredential mapToDomainCredential(PasskeyCredentialEntity entity) {
        if (entity == null) { return null; }

        String credentialIdStr = Base64.getUrlEncoder().withoutPadding().encodeToString(entity.getCredentialId());
        String publicKeyStr = Base64.getUrlEncoder().withoutPadding().encodeToString(entity.getPublicKey());

        return new PasskeyCredential(credentialIdStr, publicKeyStr, entity.getSignCount());
    }

    private PasskeyCredentialEntity mapToEntityCredential(PasskeyCredential domain) {
        if (domain == null) { return null; }

        PasskeyCredentialEntity entity = new PasskeyCredentialEntity();
        entity.setCredentialId(Base64.getUrlDecoder().decode(domain.getCredentialId()));
        entity.setPublicKey(Base64.getUrlDecoder().decode(domain.getPublicKey()));
        entity.setSignCount(domain.getSignCount());

        return entity;
    }
}
