package aleosh.online.vivia.features.users.users.services.impl;

import aleosh.online.vivia.features.users.lessor.domain.entities.Lessor;
import aleosh.online.vivia.features.users.lessor.domain.repositories.ILessorRepository;
import aleosh.online.vivia.features.users.users.data.dtos.request.UpdateUserEmailRequestDto;
import aleosh.online.vivia.features.users.users.data.dtos.request.UpdateUserNameRequestDto;
import aleosh.online.vivia.features.users.users.data.dtos.response.UserProfileResponseDto;
import aleosh.online.vivia.features.users.users.data.repositories.UserRepository;
import aleosh.online.vivia.features.users.users.domain.entities.User;
import aleosh.online.vivia.features.users.users.domain.exceptions.UserAlreadyExistsException;
import aleosh.online.vivia.features.users.users.domain.exceptions.UserNotFoundException;
import aleosh.online.vivia.features.users.users.domain.repositories.IUserRepository;
import aleosh.online.vivia.features.users.users.services.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;
    private final ILessorRepository lessorRepository;
    private final UserRepository userJpaRepository;

    public UserServiceImpl(IUserRepository userRepository, ILessorRepository lessorRepository, UserRepository userJpaRepository) {
        this.userRepository = userRepository;
        this.lessorRepository = lessorRepository;
        this.userJpaRepository = userJpaRepository;
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

    @Override
    @Transactional
    public void updateName(UUID userId, UpdateUserNameRequestDto dto) {
        if (!userJpaRepository.existsById(userId)) {
            throw new UserNotFoundException("User " + userId + " not found.");
        }
        userJpaRepository.updateName(userId, dto.getName(), dto.getPaternalSurname(), dto.getMaternalSurname());
    }

    @Override
    @Transactional
    public void updateEmail(UUID userId, UpdateUserEmailRequestDto dto) {
        if (userJpaRepository.existsByEmail(dto.getEmail())) {
            throw new UserAlreadyExistsException("El correo " + dto.getEmail() + " ya está registrado.");
        }
        if (!userJpaRepository.existsById(userId)) {
            throw new UserNotFoundException("User " + userId + " not found.");
        }
        userJpaRepository.updateEmail(userId, dto.getEmail());
    }
}
