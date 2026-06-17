package aleosh.online.vivia.features.users.users.data.repositories;

import aleosh.online.vivia.features.users.users.data.entities.UserEntity;
import aleosh.online.vivia.features.users.users.data.mappers.UserMapper;
import aleosh.online.vivia.features.users.users.domain.entities.User;
import aleosh.online.vivia.features.users.users.domain.exceptions.UserAlreadyExistsException;
import aleosh.online.vivia.features.users.users.domain.exceptions.UserNotFoundException;
import aleosh.online.vivia.features.users.users.domain.repositories.IUserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryAdapter implements IUserRepository {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserRepositoryAdapter(
            UserRepository userRepository,
            @org.springframework.beans.factory.annotation.Qualifier("userDataMapper") UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }


    @Override
    public User save(User user) {
        Optional<UserEntity> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
            throw new UserAlreadyExistsException("Email " + user.getEmail() + " is already taken.");
        }

        UserEntity userEntity = userMapper.toEntity(user);
        UserEntity savedUserEntity = userRepository.save(userEntity);
        return userMapper.toDomain(savedUserEntity);
    }

    @Override
    public void deleteById(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User " + id + " not found.");
        }
        userRepository.deleteById(id);
    }
}
