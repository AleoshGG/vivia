package aleosh.online.vivia.features.users.lessor.services;

import aleosh.online.vivia.features.auth.data.dtos.response.AuthResponseDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.request.RegisterLessorBiometricChallengeDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.request.RegisterLessorBiometricVerifyDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.request.RegisterLessorGoogleDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.request.RegisterLessorPasswordDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.request.UpdateLessorPhoneRequestDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.request.VerificationUploadRequestDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.response.VerificationStatusResponseDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.response.VerificationUploadResponseDto;
import aleosh.online.vivia.features.users.lessor.domain.objectvalues.DocumentType;

import java.util.UUID;

public interface ILessorService {
    AuthResponseDto registerWithPassword(RegisterLessorPasswordDto request);
    AuthResponseDto registerWithGoogleAccount(RegisterLessorGoogleDto request);
    String startBiometricRegistration(RegisterLessorBiometricChallengeDto dto);
    AuthResponseDto finishBiometricRegistration(RegisterLessorBiometricVerifyDto dto);
    void updatePhoneNumber(UUID lessorId, UpdateLessorPhoneRequestDto dto);
    VerificationUploadResponseDto requestVerificationUpload(UUID lessorId, VerificationUploadRequestDto dto);
    void saveVerificationDocument(UUID lessorId, DocumentType documentType, String publicUrl);
    VerificationStatusResponseDto getVerificationStatus(UUID lessorId);
    void resetVerificationStatus(UUID lessorId);
}
