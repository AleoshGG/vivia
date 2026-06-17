package aleosh.online.vivia.features.users.lessee.services.impl;

import aleosh.online.vivia.features.users.lessee.data.dtos.request.CreateLesseeDto;
import aleosh.online.vivia.features.users.lessee.data.dtos.request.VerifyLesseeRegistrationDto;
import aleosh.online.vivia.features.users.lessee.data.dtos.response.LesseeResponseDto;
import aleosh.online.vivia.features.users.lessee.data.dtos.response.LessorWithFollowStatusDto;
import aleosh.online.vivia.features.users.lessee.services.ILesseeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LesseeServiceImpl implements ILesseeService {

    @Override
    public String startRegistration(CreateLesseeDto createLesseeDto) {
        return null;
    }

    @Override
    public LesseeResponseDto finishRegistration(VerifyLesseeRegistrationDto verifyLesseeRegistrationDto) {
        return null;
    }

    @Override
    public LesseeResponseDto getLesseeByUsername(String username) {
        return null;
    }

    @Override
    public LesseeResponseDto getLesseeByEmail(String email) {
        return null;
    }

    @Override
    public List<LesseeResponseDto> getAllLessees() {
        return null;
    }

    @Override
    public void followLessor(String lesseeEmail, String lessorCompanyName) {
    }

    @Override
    public void updateFcmToken(String email, String fcmToken) {
    }

    @Override
    public List<LessorWithFollowStatusDto> getAllLessorsWithFollowStatus(String email) {
        return null;
    }
}