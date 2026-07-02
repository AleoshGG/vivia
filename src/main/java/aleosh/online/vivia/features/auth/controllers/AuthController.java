package aleosh.online.vivia.features.auth.controllers;

import aleosh.online.vivia.features.auth.data.dtos.request.BiometricLoginChallengeDto;
import aleosh.online.vivia.features.auth.data.dtos.request.GoogleLoginRequestDto;
import aleosh.online.vivia.features.auth.data.dtos.request.LoginRequestDto;
import aleosh.online.vivia.features.auth.data.dtos.request.RefreshTokenRequestDto;
import aleosh.online.vivia.core.dtos.BaseResponse;
import aleosh.online.vivia.features.auth.data.dtos.request.VerifyLoginDto;
import aleosh.online.vivia.features.auth.data.dtos.response.AuthResponseDto;
import aleosh.online.vivia.features.auth.services.IAuthService;
import aleosh.online.vivia.features.auth.services.impl.RefreshTokenServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    private final RefreshTokenServiceImpl refreshTokenServiceImpl;

    @Autowired
    public AuthController(IAuthService authService, RefreshTokenServiceImpl refreshTokenServiceImpl) {
        this.authService = authService;
        this.refreshTokenServiceImpl = refreshTokenServiceImpl;
    }

    @Operation(
            summary = "Paso 1: Solicitar desafío de login",
            description = """
                    Devuelve las opciones WebAuthn para solicitar la huella digital al usuario. Requiere el email del usuario.

                    El campo `data` contiene un objeto `PublicKeyCredentialRequestOptions` serializado con la siguiente estructura:
                    - `publicKey.challenge`: cadena Base64URL única y de un solo uso para prevenir ataques de repetición.
                    - `publicKey.rpId`: identificador del Relying Party (dominio de la API).
                    - `publicKey.allowCredentials`: lista de credenciales biométricas registradas por el usuario. Cada elemento incluye `id` (Base64URL) y `type` ("public-key").
                    - `publicKey.userVerification`: nivel de verificación requerido ("preferred", "required" o "discouraged").
                    - `publicKey.timeout`: tiempo máximo en milisegundos para completar la autenticación.
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Challenge de login WebAuthn generado exitosamente.",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "Respuesta exitosa",
                            value = """
                                    {
                                      "success": true,
                                      "data": {
                                        "publicKey": {
                                          "challenge": "y6c8iTMikxA_RpMmjNPxZMg4ag4dLM_Xda7AfXjJNNs",
                                          "rpId": "vivia-api.aleosh.online",
                                          "allowCredentials": [
                                            {
                                              "type": "public-key",
                                              "id": "Ac7pN9Kf2Lx..."
                                            }
                                          ],
                                          "userVerification": "preferred",
                                          "timeout": 60000
                                        }
                                      },
                                      "message": "Desafío de login generado correctamente",
                                      "status": "OK"
                                    }
                                    """
                    )
            )
    )
    @PostMapping(value = "/login/challenge", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BaseResponse<String>> startLogin(
            @Valid @RequestBody BiometricLoginChallengeDto dto
    ) {
        String optionsJson = authService.startLogin(dto);

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

    @Operation(summary = "Inicio de sesión tradicional", description = "Autentica al correo y contraseña, devolviendo el JWT.")
    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BaseResponse<AuthResponseDto>> traditionalLogin(
            @Valid @RequestBody LoginRequestDto loginDto
    ) {
        AuthResponseDto authResponseDto = authService.traditionalLogin(loginDto);

        return new BaseResponse<>(
                true, authResponseDto, "Usuario logueado correctamente", HttpStatus.OK
        ).buildResponseEntity();
    }

    @Operation(summary = "Inicio de sesión administrador", description = "Autentica un administrador con correo y contraseña. Rechaza credenciales de usuarios no administradores.")
    @PostMapping(value = "/login/admin", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BaseResponse<AuthResponseDto>> adminLogin(
            @Valid @RequestBody LoginRequestDto loginDto
    ) {
        AuthResponseDto authResponseDto = authService.adminLogin(loginDto);

        return new BaseResponse<>(
                true, authResponseDto, "Administrador logueado correctamente", HttpStatus.OK
        ).buildResponseEntity();
    }

    @Operation(summary = "Inicio de sesión con Google",
            description = "Autentica un usuario existente usando su cuenta de Google.")
    @PostMapping(value = "/login/google", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BaseResponse<AuthResponseDto>> googleLogin(
            @Valid @RequestBody GoogleLoginRequestDto requestDto
    ) {
        AuthResponseDto authResponseDto = authService.googleLogin(requestDto);

        return new BaseResponse<>(
                true, authResponseDto, "Usuario autenticado con Google", HttpStatus.OK
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
        refreshTokenServiceImpl.deleteByUserIdentifier(identifier);

        return new BaseResponse<Void>(
                true, null, "Sesión cerrada correctamente", HttpStatus.OK
        ).buildResponseEntity();
    }
}