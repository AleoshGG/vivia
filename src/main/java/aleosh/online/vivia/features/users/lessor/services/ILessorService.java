package aleosh.online.vivia.features.users.lessor.services;

import aleosh.online.vivia.features.auth.data.dtos.response.AuthResponseDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.request.RegisterLessorBiometricChallengeDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.request.RegisterLessorBiometricVerifyDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.request.RegisterLessorGoogleDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.request.RegisterLessorPasswordDto;

public interface ILessorService {
    AuthResponseDto registerWithPassword(RegisterLessorPasswordDto request);
    AuthResponseDto registerWithGoogleAccount(RegisterLessorGoogleDto request);

    String startBiometricRegistration(RegisterLessorBiometricChallengeDto dto);
    AuthResponseDto finishBiometricRegistration(RegisterLessorBiometricVerifyDto dto);
}