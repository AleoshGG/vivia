package aleosh.online.vivia.features.auth.domain.repositories;

import aleosh.online.vivia.features.auth.domain.entities.Credential;

import java.util.UUID;

public interface ICredentialRepository {

    void save(Credential credential);
    void deleteById(UUID id);

}
