package aleosh.online.vivia.features.users.users.services.impl;

import aleosh.online.vivia.features.users.users.domain.entities.User;
import aleosh.online.vivia.features.users.users.domain.exceptions.UserNotFoundException;
import aleosh.online.vivia.features.users.users.domain.repositories.IUserRepository;
import aleosh.online.vivia.features.users.users.services.IUserService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;

    public UserServiceImpl(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getMe(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User " + userId + " not found."));
    }
}
