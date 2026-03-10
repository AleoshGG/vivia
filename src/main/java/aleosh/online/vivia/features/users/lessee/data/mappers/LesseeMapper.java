package aleosh.online.vivia.features.users.lessee.data.mappers;

import aleosh.online.vivia.features.users.lessee.data.entities.LesseeEntity;
import aleosh.online.vivia.features.users.lessee.domain.entities.Lessee;
import aleosh.online.vivia.features.users.lessor.data.entities.PasskeyCredentialEntity;
import aleosh.online.vivia.features.users.lessor.domain.valueobjects.PasskeyCredential;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component("lesseeDataMapper")
public class LesseeMapper {
    public Lessee toDomain(LesseeEntity lesseeEntity) {
        if (lesseeEntity == null) { return null; }

        Lessee.Builder builder = Lessee.builder()
                .id(lesseeEntity.getId())
                .userHandle(lesseeEntity.getUserHandle())
                .username(lesseeEntity.getUsername())
                .email(lesseeEntity.getEmail());

        if (lesseeEntity.getCredentials() != null) {
            for (PasskeyCredentialEntity credEntity : lesseeEntity.getCredentials()) {
                builder.addCredential(mapToDomainCredential(credEntity));
            }
        }

        return builder.build();
    }

    public LesseeEntity toEntity(Lessee lessee) {
        if (lessee == null) { return null; }

        LesseeEntity lesseeEntity = new LesseeEntity();
        lesseeEntity.setId(lessee.getId());
        lesseeEntity.setUserHandle(lessee.getUserHandle());
        lesseeEntity.setUsername(lessee.getUsername());
        lesseeEntity.setEmail(lessee.getEmail());
        
        if (lessee.getCredentials() != null) {
            for (PasskeyCredential cred : lessee.getCredentials()) {
                lesseeEntity.addCredential(mapToEntityCredential(cred));
            }
        }

        return lesseeEntity;
    }

    private PasskeyCredential mapToDomainCredential(PasskeyCredentialEntity entity) {
        if (entity == null) { return null; }

        String credentialIdStr = Base64.getUrlEncoder().withoutPadding().encodeToString(entity.getCredentialId());
        String publicKeyStr = Base64.getUrlEncoder().withoutPadding().encodeToString(entity.getPublicKey());

        return new PasskeyCredential(credentialIdStr, publicKeyStr, entity.getSignCount());
    }

    private PasskeyCredentialEntity mapToEntityCredential(PasskeyCredential domain) {
        if (domain == null) { return null; }

        PasskeyCredentialEntity entity = new
                PasskeyCredentialEntity();
        entity.setCredentialId(Base64.getUrlDecoder().decode(domain.getCredentialId()));
        entity.setPublicKey(Base64.getUrlDecoder().decode(domain.getPublicKey()));
        entity.setSignCount(domain.getSignCount());

        return entity;
    }
}