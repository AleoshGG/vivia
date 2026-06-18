package aleosh.online.vivia.features.users.lessee.services;

import aleosh.online.vivia.features.auth.data.dtos.response.AuthResponseDto;
import aleosh.online.vivia.features.users.lessee.data.dtos.request.RegisterLesseeBiometricChallengeDto;
import aleosh.online.vivia.features.users.lessee.data.dtos.request.RegisterLesseeBiometricVerifyDto;
import aleosh.online.vivia.features.users.lessee.data.dtos.request.RegisterLesseeGoogleDto;
import aleosh.online.vivia.features.users.lessee.data.dtos.request.RegisterLesseePasswordDto;

public interface ILesseeService {

    AuthResponseDto registerWithPassword(RegisterLesseePasswordDto request);
    AuthResponseDto registerWithGoogleAccount(RegisterLesseeGoogleDto request);

    String startBiometricRegistration(RegisterLesseeBiometricChallengeDto dto);
    AuthResponseDto finishBiometricRegistration(RegisterLesseeBiometricVerifyDto dto);
}