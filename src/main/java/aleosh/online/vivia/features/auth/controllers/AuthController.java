package aleosh.online.vivia.features.auth.controllers;

import aleosh.online.vivia.core.dtos.BaseResponse;
import aleosh.online.vivia.features.auth.data.dtos.request.VerifyLoginDto;
import aleosh.online.vivia.features.auth.data.dtos.response.AuthResponseDto;
import aleosh.online.vivia.features.auth.services.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Endpoints para la gestión de sesiones mediante biometría y JWT.")
public class AuthController {

    private final IAuthService authService;

    @Autowired
    public AuthController(IAuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Paso 1: Solicitar desafío de login", description = "Devuelve las opciones WebAuthn para solicitar la huella digital al usuario. No requiere identificador.")
    @PostMapping(value = "/login/challenge", produces = "application/json")
    public ResponseEntity<BaseResponse<String>> startLogin() {
        String optionsJson = authService.startLogin();

        return new BaseResponse<>(
                true, optionsJson, "Desafío de login generado correctamente", HttpStatus.OK
        ).buildResponseEntity();
    }

    @Operation(summary = "Paso 2: Verificar credencial y loguear", description = "Recibe la respuesta biométrica, la valida, y si es correcta devuelve el token JWT con los roles correspondientes.")
    @PostMapping(value = "/login/verify", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BaseResponse<AuthResponseDto>> finishLogin(
            @RequestBody VerifyLoginDto verifyLoginDto
    ) {
        AuthResponseDto authResponseDto = authService.finishLogin(verifyLoginDto);

        return new BaseResponse<>(
                true, authResponseDto, "Usuario logueado correctamente", HttpStatus.OK
        ).buildResponseEntity();
    }
}