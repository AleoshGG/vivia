package aleosh.online.vivia.features.users.lessor.controllers;

import aleosh.online.vivia.core.dtos.BaseResponse;
import aleosh.online.vivia.features.auth.data.dtos.response.AuthResponseDto;
//import aleosh.online.vivia.features.users.lessor.data.dtos.request.CreateLessorDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.request.RegisterLessorBiometricChallengeDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.request.RegisterLessorBiometricVerifyDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.request.RegisterLessorGoogleDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.request.RegisterLessorPasswordDto;
//import aleosh.online.vivia.features.users.lessor.data.dtos.request.VerifyLessorRegistrationDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.response.LessorResponseDto;
import aleosh.online.vivia.features.users.lessor.services.ILessorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import aleosh.online.vivia.features.users.lessee.data.dtos.response.LesseeResponseDto;

@RestController
@RequestMapping("/lessors")
@Tag(name = "Gestión de arrendadores", description = "Endpoints para crear, listar y buscar arrendadores.")
public class LessorController {

    private final ILessorService lessorService;

    @Autowired
    public LessorController(ILessorService lessorService) {
        this.lessorService = lessorService;
    }

    @Operation(summary = "Registro con Contraseña",
            description = "Registra un nuevo arrendador utilizando correo y contraseña. Devuelve los tokens de sesión inmediatamente.")
    @PostMapping(value = "/password", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BaseResponse<AuthResponseDto>> registerWithPassword(
            @Valid @RequestBody RegisterLessorPasswordDto requestDto
    ) {
        AuthResponseDto authResponseDto = lessorService.registerWithPassword(requestDto);

        return new BaseResponse<>(
                true, authResponseDto, "Arrendador registrado y logueado exitosamente", HttpStatus.CREATED
        ).buildResponseEntity();
    }

    @Operation(
            summary = "Registro con cuenta Google",
            description = "Registra un nuevo arrendador utilizando su cuenta vinculada a Google. Devuelve los tokens de sesión inmediatamente."
    )
    @PostMapping(value = "/google", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BaseResponse<AuthResponseDto>> registerWithGoogle(
            @Valid @RequestBody RegisterLessorGoogleDto requestDto
    ) {
        AuthResponseDto authResponseDto = lessorService.registerWithGoogleAccount(requestDto);

        return new BaseResponse<>(
                true, authResponseDto, "Arrendador registrado y logueado exitosamente", HttpStatus.CREATED
        ).buildResponseEntity();
    }

    @Operation(
            summary = "Paso 1: Solicitar desafío de registro biométrico",
            description = "Genera un challenge WebAuthn para iniciar el registro con huella digital."
    )
    @PostMapping(value = "/biometric/challenge", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BaseResponse<String>> startBiometricRegistration(
            @Valid @RequestBody RegisterLessorBiometricChallengeDto requestDto
    ) {
        String challengeJson = lessorService.startBiometricRegistration(requestDto);

        return new BaseResponse<>(
                true, challengeJson, "Challenge de registro biométrico generado", HttpStatus.OK
        ).buildResponseEntity();
    }

    @Operation(
            summary = "Paso 2: Verificar credencial biométrica y registrar",
            description = "Verifica la credencial biométrica capturada, crea el usuario y devuelve los tokens de sesión."
    )
    @PostMapping(value = "/biometric/verify", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BaseResponse<AuthResponseDto>> finishBiometricRegistration(
            @Valid @RequestBody RegisterLessorBiometricVerifyDto requestDto
    ) {
        AuthResponseDto authResponseDto = lessorService.finishBiometricRegistration(requestDto);

        return new BaseResponse<>(
                true, authResponseDto, "Arrendador registrado y logueado exitosamente", HttpStatus.CREATED
        ).buildResponseEntity();
    }
}