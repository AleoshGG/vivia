package aleosh.online.vivia.features.users.lessor.controllers;

import aleosh.online.vivia.core.dtos.BaseResponse;
import aleosh.online.vivia.features.users.lessor.data.dtos.request.CreateLessorDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.request.VerifyLessorRegistrationDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.response.LessorResponseDto;
import aleosh.online.vivia.features.users.lessor.services.ILessorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lessors")
@Tag(name = "Gestión de arrendadores", description = "Endpoints para crear, listar y buscar arrendadores.")
public class LessorController {

    private final ILessorService lessorService;

    @Autowired
    public LessorController(ILessorService lessorService) {
        this.lessorService = lessorService;
    }

    @Operation(summary = "Paso 1: Solicitar desafío de registro",
            description = "Inicia el registro del arrendador y devuelve un JSON con el desafío (Challenge) para crear la Passkey.")
    @PostMapping(value = "/register/challenge", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BaseResponse<String>> startRegistration(
            @RequestBody CreateLessorDto createLessorDto
    ) {
        // Retorna un JSON String directamente con los datos criptográficos requeridos por el celular
        String webAuthnOptionsJson = lessorService.startRegistration(createLessorDto);

        return new BaseResponse<>(
                true, webAuthnOptionsJson, "Desafío generado correctamente", HttpStatus.OK
        ).buildResponseEntity();
    }

    @Operation(summary = "Paso 2: Verificar credencial y guardar arrendador",
            description = "Recibe la firma biométrica del dispositivo, la verifica y, si es válida, persiste el usuario en base de datos.")
    @PostMapping(value = "/register/verify", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BaseResponse<LessorResponseDto>> finishRegistration(
            @RequestBody VerifyLessorRegistrationDto verifyLessorRegistrationDto
    ) {
        LessorResponseDto lessorResponseDto = lessorService.finishRegistration(verifyLessorRegistrationDto);

        return new BaseResponse<>(
                true, lessorResponseDto, "Arrendador registrado exitosamente", HttpStatus.CREATED
        ).buildResponseEntity();
    }

    @Operation(summary = "Obtener todos los arrendadores", description = "Devuelve una lista con todos los arrendadores registrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    })
    @GetMapping(produces = "application/json")
    public ResponseEntity<BaseResponse<List<LessorResponseDto>>> getAllLessors() {
        List<LessorResponseDto> lessors = lessorService.getAllLessors();

        return new BaseResponse<>(
                true, lessors, "Arrendadores obtenidos correctamente", HttpStatus.OK
        ).buildResponseEntity();
    }

    @Operation(summary = "Obtener mi perfil", description = "Devuelve los datos del arrendador autenticado usando el token de sesión.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Arrendador encontrado"),
            @ApiResponse(responseCode = "404", description = "Arrendador no encontrado", content = @Content)
    })
    @GetMapping(value = "/me", produces = "application/json")
    public ResponseEntity<BaseResponse<LessorResponseDto>> getMyProfile() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        LessorResponseDto user = lessorService.getLessorByUsername(currentUsername);

        return new BaseResponse<>(
                true, user, "Perfil obtenido correctamente", HttpStatus.OK
        ).buildResponseEntity();
    }

    @Operation(summary = "Buscar arrendador por nombre de empresa", description = "Busca y devuelve los datos de un arrendador específico mediante el nombre de su empresa.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Arrendador encontrado"),
            @ApiResponse(responseCode = "404", description = "Arrendador no encontrado", content = @Content)
    })
    @GetMapping(value = "/company/{companyName}", produces = "application/json")
    public ResponseEntity<BaseResponse<LessorResponseDto>> getLessorByCompanyName(
            @Parameter(description = "Nombre de la empresa del arrendador", required = true)
            @PathVariable String companyName
    ) {
        LessorResponseDto lessor = lessorService.getLessorByCompanyName(companyName);

        return new BaseResponse<>(
                true, lessor, "Arrendador obtenido correctamente", HttpStatus.OK
        ).buildResponseEntity();
    }

}