package aleosh.online.vivia.features.users.lessee.services;

import aleosh.online.vivia.features.users.lessee.data.dtos.request.CreateLesseeDto;
import aleosh.online.vivia.features.users.lessee.data.dtos.request.VerifyLesseeRegistrationDto;
import aleosh.online.vivia.features.users.lessee.data.dtos.response.LesseeResponseDto;

import java.util.List;

public interface ILesseeService {
    String startRegistration(CreateLesseeDto createLesseeDto);
    LesseeResponseDto finishRegistration(VerifyLesseeRegistrationDto verifyLesseeRegistrationDto);
    LesseeResponseDto getLesseeByUsername(String username);
    LesseeResponseDto getLesseeByEmail(String email);
    List<LesseeResponseDto> getAllLessees();
    void followLessor(String lesseeEmail, String lessorCompanyName);
    void updateFcmToken(String email, String fcmToken);
}