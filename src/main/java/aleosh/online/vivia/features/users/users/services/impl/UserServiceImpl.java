package aleosh.online.vivia.features.users.users.services.impl;

import aleosh.online.vivia.features.users.lessor.domain.entities.Lessor;
import aleosh.online.vivia.features.users.lessor.domain.repositories.ILessorRepository;
import aleosh.online.vivia.features.users.users.data.dtos.response.UserProfileResponseDto;
import aleosh.online.vivia.features.users.users.domain.entities.User;
import aleosh.online.vivia.features.users.users.domain.exceptions.UserNotFoundException;
import aleosh.online.vivia.features.users.users.domain.repositories.IUserRepository;
import aleosh.online.vivia.features.users.users.services.IUserService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;
    private final ILessorRepository lessorRepository;

    public UserServiceImpl(IUserRepository userRepository, ILessorRepository lessorRepository) {
        this.userRepository = userRepository;
        this.lessorRepository = lessorRepository;
    }

    @Override
    public User getMe(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User " + userId + " not found."));
    }

    @Override
    public UserProfileResponseDto getProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User " + userId + " not found."));

        Optional<Lessor> lessor = lessorRepository.getById(userId);

        String verificationStatus = lessor
                .map(l -> l.getVerificationStatus().name())
                .orElse(null);

        return new UserProfileResponseDto(
                user.getName(),
                user.getPaternalSurname(),
                user.getMaternalSurname(),
                user.getEmail(),
                user.getPhotoUrl(),
                verificationStatus
        );
    }
}
