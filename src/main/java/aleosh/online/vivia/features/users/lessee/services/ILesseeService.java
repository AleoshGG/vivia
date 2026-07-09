package aleosh.online.vivia.features.users.lessee.services;

import aleosh.online.vivia.features.auth.data.dtos.response.AuthResponseDto;
import aleosh.online.vivia.features.users.lessee.data.dtos.request.RegisterLesseeBiometricChallengeDto;
import aleosh.online.vivia.features.users.lessee.data.dtos.request.RegisterLesseeBiometricVerifyDto;
import aleosh.online.vivia.features.users.lessee.data.dtos.request.RegisterLesseeGoogleDto;
import aleosh.online.vivia.features.users.lessee.data.dtos.request.RegisterLesseePasswordDto;
import aleosh.online.vivia.features.users.lessee.data.dtos.request.UpdateLesseeUbicationDto;
import aleosh.online.vivia.features.users.lessee.data.dtos.response.LesseeUbicationResponseDto;

import java.util.UUID;

public interface ILesseeService {

    AuthResponseDto registerWithPassword(RegisterLesseePasswordDto request);
    AuthResponseDto registerWithGoogleAccount(RegisterLesseeGoogleDto request);

    String startBiometricRegistration(RegisterLesseeBiometricChallengeDto dto);
    AuthResponseDto finishBiometricRegistration(RegisterLesseeBiometricVerifyDto dto);

    void updateUbication(UUID lesseeId, UpdateLesseeUbicationDto dto);
    LesseeUbicationResponseDto getUbication(UUID lesseeId);
}