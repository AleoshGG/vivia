package aleosh.online.vivia.features.auth.data.mappers;

import aleosh.online.vivia.features.auth.data.entities.CredentialEntity;
import aleosh.online.vivia.features.auth.domain.entities.Credential;
import aleosh.online.vivia.features.users.users.data.entities.UserEntity;
import org.springframework.stereotype.Component;

@Component("credentialDataMapper")
public class CredentialMapper {

    public Credential toDomain(CredentialEntity credentialEntity) {
        if (credentialEntity == null) { return null; }


        Credential.Builder builder = Credential.build()
                .id(credentialEntity.getId())
                .userId(credentialEntity.getUser().getId())
                .credentialType(credentialEntity.getCredentialType())
                .providerCredentialType(credentialEntity.getProviderCredentialId())
                .secretData(credentialEntity.getSecretData())
                .createdAt(credentialEntity.getCreatedAt())
                .updatedAt(credentialEntity.getUpdatedAt());

        return builder.build();
    }

    public CredentialEntity toEntity(Credential credential) {
        if (credential == null) { return null; }

        CredentialEntity credentialEntity = new CredentialEntity();
        credentialEntity.setId(credential.getId());
        credentialEntity.setCredentialType(credential.getCredentialType());
        credentialEntity.setProviderCredentialId(credential.getProviderCredentialId());
        credentialEntity.setSecretData(credential.getSecretData());
        credentialEntity.setCreatedAt(credential.getCreatedAt());
        credentialEntity.setUpdatedAt(credential.getUpdatedAt());

        UserEntity userRef = new UserEntity();
        userRef.setId(credential.getUserId());
        credentialEntity.setUser(userRef);

        return credentialEntity;
    }
}
