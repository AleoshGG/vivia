package aleosh.online.vivia.features.users.lessee.services;

import aleosh.online.vivia.features.users.lessee.data.dtos.request.CreateLesseeDto;
import aleosh.online.vivia.features.users.lessee.data.dtos.response.LesseeResponseDto;

import java.util.List;

public interface ILesseeService {
    LesseeResponseDto createLessee(CreateLesseeDto createLesseeDto);
    LesseeResponseDto getLesseeByUsername(String username);
    LesseeResponseDto getLesseeByEmail(String email);
    List<LesseeResponseDto> getAllLessees();
}