package aleosh.online.vivia.features.users.users.domain.repositories;

import aleosh.online.vivia.features.users.users.domain.entities.User;

import java.util.UUID;

public interface IUserRepository {

    User save(User user);
    void deleteById(UUID id);


}
