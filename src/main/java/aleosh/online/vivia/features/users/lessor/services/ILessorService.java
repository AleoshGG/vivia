package aleosh.online.vivia.features.users.lessor.services;


import aleosh.online.vivia.features.users.lessor.data.dtos.request.CreateLessorDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.response.LessorResponseDto;

import java.util.List;
//import org.springframework.web.multipart.MultipartFile;

public interface ILessorService {
    LessorResponseDto createLessor(CreateLessorDto createLessorDto);
    LessorResponseDto getLessorByCompanyName(String companyName);
    LessorResponseDto getLessorByUsername(String username);
    List<LessorResponseDto> getAllLessors();
}
