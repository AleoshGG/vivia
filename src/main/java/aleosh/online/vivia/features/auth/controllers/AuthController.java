package aleosh.online.vivia.features.auth.controllers;

import aleosh.online.mediaserver.auth.data.dtos.request.AuthRequestDto;
import aleosh.online.mediaserver.auth.data.dtos.response.AuthResponseDto;
import aleosh.online.mediaserver.auth.services.IAuthService;
import aleosh.online.mediaserver.core.dtos.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Endpoints para la gestión de sesiones y tokens JWT.")
public class AuthController {

    private final IAuthService authService;

    @Autowired
    public AuthController(IAuthService authService) {this.authService = authService;}

    @Operation(summary = "Iniciar sesión", description = "Autentica a un usuario mediante usuario y contraseña, devolviendo un token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso, token generado"),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas (Usuario o contraseña inválidos)", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AuthResponseDto>> login(
            @RequestBody AuthRequestDto authRequestDto
    ) {
        AuthResponseDto authResponseDto = authService.login(authRequestDto);

        BaseResponse<AuthResponseDto> response = new BaseResponse<>(
                true, authResponseDto, "Usuario logueado correctamente", HttpStatus.OK
        );

        return response.buildResponseEntity();
    }

}

