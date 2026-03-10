package aleosh.online.vivia.features.users.lessor.controllers;

import aleosh.online.vivia.core.dtos.BaseResponse;
import aleosh.online.vivia.features.users.lessor.data.dtos.request.CreateLessorDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.response.LessorResponseDto;
import aleosh.online.vivia.features.users.lessor.services.ILessorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Operation(summary = "Registrar un nuevo arrendador",
            description = "Crea un arrendador enviando un JSON con sus datos básicos y la credencial biométrica (Passkey).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Arrendador creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada o credencial inválidos", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<BaseResponse<LessorResponseDto>> createLessor(
            @Parameter(description = "Datos del arrendador incluyendo la credencial WebAuthn", required = true, schema = @Schema(implementation = CreateLessorDto.class))
            @RequestBody CreateLessorDto createLessorDto
    ) {
        LessorResponseDto lessorResponseDto = lessorService.createLessor(createLessorDto);

        return new BaseResponse<>(
                true, lessorResponseDto, "Arrendador registrado correctamente", HttpStatus.CREATED
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