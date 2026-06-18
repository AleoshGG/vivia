package aleosh.online.vivia.features.auth.data.repositories;

import aleosh.online.vivia.features.auth.data.entities.CredentialEntity;
import aleosh.online.vivia.features.auth.data.mappers.CredentialMapper;
import aleosh.online.vivia.features.auth.domain.entities.Credential;
import aleosh.online.vivia.features.auth.domain.exceptions.CredentialNotFoundException;
import aleosh.online.vivia.features.auth.domain.repositories.ICredentialRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class CredentialRepositoryAdapter implements ICredentialRepository {

    private final CredentialRepository credentialRepository;
    private final CredentialMapper credentialMapper;

    public CredentialRepositoryAdapter(
            CredentialRepository credentialRepository,
            @org.springframework.beans.factory.annotation.Qualifier("credentialDataMapper") CredentialMapper credentialMapper
    ) {
        this.credentialRepository = credentialRepository;
        this.credentialMapper = credentialMapper;
    }

    @Override
    public void save(Credential credential) {
        CredentialEntity credentialEntity = credentialMapper.toEntity(credential);
        credentialRepository.save(credentialEntity);
    }

    @Override
    public void deleteById(UUID id) {
        if (!credentialRepository.existsById(id)) {
            throw new CredentialNotFoundException("Credential not found.");
        }

        credentialRepository.deleteById(id);
    }
}
