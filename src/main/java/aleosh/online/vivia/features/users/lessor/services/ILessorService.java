package aleosh.online.vivia.features.users.lessor.services;

import aleosh.online.vivia.features.users.lessor.data.dtos.request.CreateLessorDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.request.VerifyLessorRegistrationDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.response.LessorResponseDto;
import java.util.List;

public interface ILessorService {
    // Paso 1: Inicia el registro y devuelve las opciones WebAuthn (Challenge) en formato JSON
    String startRegistration(CreateLessorDto createLessorDto);

    // Paso 2: Verifica la firma criptográfica y guarda el Lessor en la base de datos
    LessorResponseDto finishRegistration(VerifyLessorRegistrationDto verifyDto);

    LessorResponseDto getLessorByCompanyName(String companyName);
    LessorResponseDto getLessorByUsername(String username);
    List<LessorResponseDto> getAllLessors();
}