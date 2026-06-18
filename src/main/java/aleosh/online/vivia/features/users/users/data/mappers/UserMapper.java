package aleosh.online.vivia.features.users.users.data.mappers;

import aleosh.online.vivia.features.users.users.data.entities.UserEntity;
import aleosh.online.vivia.features.users.users.domain.entities.User;
import org.springframework.stereotype.Component;

@Component("userDataMapper")
public class UserMapper {

    public User toDomain(UserEntity userEntity) {

        if (userEntity == null) { return null; }

        User.Builder builder = User.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .paternalSurname(userEntity.getPaternalSurname())
                .maternalSurname(userEntity.getMaternalSurname())
                .email(userEntity.getEmail())
                .photoUrl(userEntity.getPhotoUrl())
                .createdAt(userEntity.getCreatedAt())
                .updatedAt(userEntity.getUpdatedAt());

        return builder.build();
    }


    public UserEntity toEntity(User user) {
        if (user == null) { return null; }

        UserEntity userEntity = new UserEntity();
        userEntity.setId(user.getId());
        userEntity.setName(user.getName());
        userEntity.setPaternalSurname(user.getPaternalSurname());
        userEntity.setMaternalSurname(user.getMaternalSurname());
        userEntity.setEmail(user.getEmail());
        userEntity.setPhotoUrl(user.getPhotoUrl());
        userEntity.setCreatedAt(user.getCreatedAt());
        userEntity.setUpdatedAt(user.getUpdatedAt());

        return userEntity;
    }
}
