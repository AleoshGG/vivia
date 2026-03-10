package aleosh.online.vivia.features.auth.services;

import aleosh.online.vivia.features.auth.data.dtos.request.AuthRequestDto;
import aleosh.online.vivia.features.auth.data.dtos.response.AuthResponseDto;

public interface IAuthService {
    AuthResponseDto login(AuthRequestDto authRequestDto);
}
