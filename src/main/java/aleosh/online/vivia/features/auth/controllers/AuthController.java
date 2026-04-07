package aleosh.online.vivia.features.auth.controllers;

import aleosh.online.vivia.features.auth.data.dtos.request.LoginRequestDto;
import aleosh.online.vivia.features.auth.data.dtos.request.RefreshTokenRequestDto;
import aleosh.online.vivia.core.dtos.BaseResponse;
import aleosh.online.vivia.features.auth.data.dtos.request.VerifyLoginDto;
import aleosh.online.vivia.features.auth.data.dtos.response.AuthResponseDto;
import aleosh.online.vivia.features.auth.services.IAuthService;
import aleosh.online.vivia.features.auth.services.impl.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Endpoints para la gestión de sesiones mediante biometría y JWT.")
public class AuthController {

    private final IAuthService authService;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public AuthController(IAuthService authService, RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
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

    @Operation(summary = "Inicio de sesión tradicional", description = "Autentica al usuario (correo para arrendatario, empresa para arrendador) y contraseña, devolviendo el JWT.")
    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BaseResponse<AuthResponseDto>> traditionalLogin(
            @Valid @RequestBody LoginRequestDto loginDto
    ) {
        AuthResponseDto authResponseDto = authService.traditionalLogin(loginDto);

        return new BaseResponse<>(
                true, authResponseDto, "Usuario logueado correctamente", HttpStatus.OK
        ).buildResponseEntity();
    }

    @Operation(summary = "Refrescar sesión", description = "Recibe un Refresh Token válido y devuelve un nuevo par de Access Token y Refresh Token.")
    @PostMapping(value = "/refresh", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BaseResponse<AuthResponseDto>> refreshToken(
            @Valid @RequestBody RefreshTokenRequestDto request
    ) {
        AuthResponseDto response = authService.refreshToken(request);

        return new BaseResponse<>(
                true, response, "Sesión renovada exitosamente", HttpStatus.OK
        ).buildResponseEntity();
    }

    @Operation(summary = "Cerrar sesión", description = "Invalida el Refresh Token del usuario autenticado actual.")
    @PostMapping(value = "/logout", produces = "application/json")
    public ResponseEntity<BaseResponse<Void>> logout() {
        String identifier = SecurityContextHolder.getContext().getAuthentication().getName();
        refreshTokenService.deleteByUserIdentifier(identifier);

        return new BaseResponse<Void>(
                true, null, "Sesión cerrada correctamente", HttpStatus.OK
        ).buildResponseEntity();
    }
}