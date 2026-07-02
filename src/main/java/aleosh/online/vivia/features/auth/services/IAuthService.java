package aleosh.online.vivia.features.auth.services;

import aleosh.online.vivia.features.auth.data.dtos.request.BiometricLoginChallengeDto;
import aleosh.online.vivia.features.auth.data.dtos.request.GoogleLoginRequestDto;
import aleosh.online.vivia.features.auth.data.dtos.request.LoginRequestDto;
import aleosh.online.vivia.features.auth.data.dtos.request.RefreshTokenRequestDto;
import aleosh.online.vivia.features.auth.data.dtos.request.UpdatePasswordRequestDto;
import aleosh.online.vivia.features.auth.data.dtos.request.VerifyLoginDto;
import aleosh.online.vivia.features.auth.data.dtos.response.AuthResponseDto;

import java.util.UUID;

public interface IAuthService {
    String startLogin(BiometricLoginChallengeDto dto);
    AuthResponseDto finishLogin(VerifyLoginDto verifyDto);
    AuthResponseDto traditionalLogin(LoginRequestDto loginDto);
    AuthResponseDto adminLogin(LoginRequestDto loginDto);
    AuthResponseDto googleLogin(GoogleLoginRequestDto googleLoginDto);
    AuthResponseDto refreshToken(RefreshTokenRequestDto request);
    void updatePassword(UUID userId, UpdatePasswordRequestDto dto);
}
