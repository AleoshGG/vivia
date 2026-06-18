package aleosh.online.vivia.features.users.lessee.services;

import aleosh.online.vivia.features.auth.data.dtos.response.AuthResponseDto;
import aleosh.online.vivia.features.users.lessee.data.dtos.request.RegisterLesseeGoogleDto;
import aleosh.online.vivia.features.users.lessee.data.dtos.request.RegisterLesseePasswordDto;

public interface ILesseeService {

    AuthResponseDto registerWithPassword(RegisterLesseePasswordDto request);
    AuthResponseDto registerWithGoogleAccount(RegisterLesseeGoogleDto request);

    /*String startRegistration(CreateLesseeDto createLesseeDto);
    LesseeResponseDto finishRegistration(VerifyLesseeRegistrationDto verifyLesseeRegistrationDto);
    LesseeResponseDto getLesseeByUsername(String username);
    LesseeResponseDto getLesseeByEmail(String email);
    List<LesseeResponseDto> getAllLessees();
    void followLessor(String lesseeEmail, String lessorCompanyName);
    void updateFcmToken(String email, String fcmToken);
    List<LessorWithFollowStatusDto> getAllLessorsWithFollowStatus(String email);*/
}