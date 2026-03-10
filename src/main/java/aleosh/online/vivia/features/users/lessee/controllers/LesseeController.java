package aleosh.online.vivia.features.users.lessee.controllers;

import aleosh.online.vivia.core.dtos.BaseResponse;
import aleosh.online.vivia.features.users.lessee.data.dtos.request.CreateLesseeDto;
import aleosh.online.vivia.features.users.lessee.data.dtos.response.LesseeResponseDto;
import aleosh.online.vivia.features.users.lessee.services.ILesseeService;
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
@RequestMapping("/lessees")
@Tag(name = "Gestión de arrendatarios", description = "Endpoints para crear, listar y buscar arrendatarios.")
public class LesseeController {

    private final ILesseeService lesseeService;

    @Autowired
    public LesseeController(ILesseeService lesseeService) {
        this.lesseeService = lesseeService;
    }

    @Operation(summary = "Registrar un nuevo arrendatario",
            description = "Crea un arrendatario enviando un JSON con sus datos básicos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Arrendatario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<BaseResponse<LesseeResponseDto>> createLessee(
            @Parameter(description = "Datos del arrendatario", required = true, schema = @Schema(implementation = CreateLesseeDto.class))
            @RequestBody CreateLesseeDto createLesseeDto
    ) {
        LesseeResponseDto lesseeResponseDto = lesseeService.createLessee(createLesseeDto);

        return new BaseResponse<>(
                true, lesseeResponseDto, "Arrendatario registrado correctamente", HttpStatus.CREATED
        ).buildResponseEntity();
    }

    @Operation(summary = "Obtener todos los arrendatarios", description = "Devuelve una lista con todos los arrendatarios registrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    })
    @GetMapping(produces = "application/json")
    public ResponseEntity<BaseResponse<List<LesseeResponseDto>>> getAllLessees() {
        List<LesseeResponseDto> lessees = lesseeService.getAllLessees();

        return new BaseResponse<>(
                true, lessees, "Arrendatarios obtenidos correctamente", HttpStatus.OK
        ).buildResponseEntity();
    }

    @Operation(summary = "Obtener mi perfil", description = "Devuelve los datos del arrendatario autenticado usando el token de sesión.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Arrendatario encontrado"),
            @ApiResponse(responseCode = "404", description = "Arrendatario no encontrado", content = @Content)
    })
    @GetMapping(value = "/me", produces = "application/json")
    public ResponseEntity<BaseResponse<LesseeResponseDto>> getMyProfile() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        LesseeResponseDto user = lesseeService.getLesseeByUsername(currentUsername);

        return new BaseResponse<>(
                true, user, "Perfil obtenido correctamente", HttpStatus.OK
        ).buildResponseEntity();
    }

    @Operation(summary = "Buscar arrendatario por correo", description = "Busca y devuelve los datos de un arrendatario específico mediante su correo electrónico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Arrendatario encontrado"),
            @ApiResponse(responseCode = "404", description = "Arrendatario no encontrado", content = @Content)
    })
    @GetMapping(value = "/email/{email}", produces = "application/json")
    public ResponseEntity<BaseResponse<LesseeResponseDto>> getLesseeByEmail(
            @Parameter(description = "Correo electrónico del arrendatario", required = true)
            @PathVariable String email
    ) {
        LesseeResponseDto lessee = lesseeService.getLesseeByEmail(email);

        return new BaseResponse<>(
                true, lessee, "Arrendatario obtenido correctamente", HttpStatus.OK
        ).buildResponseEntity();
    }

}