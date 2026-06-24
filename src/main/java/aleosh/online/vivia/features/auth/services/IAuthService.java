package aleosh.online.vivia.features.auth.services;

import aleosh.online.vivia.features.auth.data.dtos.request.BiometricLoginChallengeDto;
import aleosh.online.vivia.features.auth.data.dtos.request.GoogleLoginRequestDto;
import aleosh.online.vivia.features.auth.data.dtos.request.LoginRequestDto;
import aleosh.online.vivia.features.auth.data.dtos.request.RefreshTokenRequestDto;
import aleosh.online.vivia.features.auth.data.dtos.request.VerifyLoginDto;
import aleosh.online.vivia.features.auth.data.dtos.response.AuthResponseDto;

public interface IAuthService {
    String startLogin(BiometricLoginChallengeDto dto);
    AuthResponseDto finishLogin(VerifyLoginDto verifyDto);

    AuthResponseDto traditionalLogin(LoginRequestDto loginDto);

    AuthResponseDto googleLogin(GoogleLoginRequestDto googleLoginDto);

    AuthResponseDto refreshToken(RefreshTokenRequestDto request);
}