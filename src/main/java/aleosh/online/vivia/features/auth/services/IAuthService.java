package aleosh.online.vivia.features.auth.services;

import aleosh.online.vivia.features.auth.data.dtos.request.VerifyLoginDto;
import aleosh.online.vivia.features.auth.data.dtos.response.AuthResponseDto;

public interface IAuthService {
    String startLogin();
    AuthResponseDto finishLogin(VerifyLoginDto verifyDto);
}