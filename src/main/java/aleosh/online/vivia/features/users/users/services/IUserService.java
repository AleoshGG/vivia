package aleosh.online.vivia.features.users.users.services;

import aleosh.online.vivia.features.users.users.data.dtos.request.UpdateUserEmailRequestDto;
import aleosh.online.vivia.features.users.users.data.dtos.request.UpdateUserNameRequestDto;
import aleosh.online.vivia.features.users.users.data.dtos.response.UserProfileResponseDto;
import aleosh.online.vivia.features.users.users.domain.entities.User;

import java.util.UUID;

public interface IUserService {
    User getMe(UUID userId);
    UserProfileResponseDto getProfile(UUID userId);
    void updateName(UUID userId, UpdateUserNameRequestDto dto);
    void updateEmail(UUID userId, UpdateUserEmailRequestDto dto);
}
