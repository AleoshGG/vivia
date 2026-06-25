package aleosh.online.vivia.features.users.users.services;

import aleosh.online.vivia.features.users.users.domain.entities.User;

import java.util.UUID;

public interface IUserService {
    User getMe(UUID userId);
}
