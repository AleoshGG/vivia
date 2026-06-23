package aleosh.online.vivia.features.users.lessee.controllers;

import aleosh.online.vivia.core.dtos.BaseResponse;
import aleosh.online.vivia.features.auth.data.dtos.response.AuthResponseDto;
import aleosh.online.vivia.features.users.lessee.data.dtos.request.RegisterLesseeBiometricChallengeDto;
import aleosh.online.vivia.features.users.lessee.data.dtos.request.RegisterLesseeBiometricVerifyDto;
import aleosh.online.vivia.features.users.lessee.data.dtos.request.RegisterLesseeGoogleDto;
import aleosh.online.vivia.features.users.lessee.data.dtos.request.RegisterLesseePasswordDto;
import aleosh.online.vivia.features.users.lessee.services.ILesseeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lessees")
@Tag(name = "Gestión de arrendatarios", description = "Endpoints para crear, listar y buscar arrendatarios.")
public class LesseeController {

    private final ILesseeService lesseeService;

    @Autowired
    public LesseeController(ILesseeService lesseeService) {
        this.lesseeService = lesseeService;
    }

    @Operation(summary = "Registro con Contraseña",
            description = "Registra un nuevo arrendatario utilizando correo y contraseña. Devuelve los tokens de sesión inmediatamente.")
    @PostMapping(value = "/password", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BaseResponse<AuthResponseDto>> registerWithPassword(
            @Valid @RequestBody RegisterLesseePasswordDto requestDto
    ) {
        AuthResponseDto authResponseDto = lesseeService.registerWithPassword(requestDto);

        return new BaseResponse<>(
                true, authResponseDto, "Arrendatario registrado y logueado exitosamente", HttpStatus.CREATED
        ).buildResponseEntity();
    }

    @Operation(
            summary = "Registro con cuenta Google",
            description = "Registra un nuevo arrendatario utilizando su cuenta vinculada a Google. Devuelve los tokens de sesión inmediatamente."
    )
    @PostMapping(value = "/google", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BaseResponse<AuthResponseDto>> registerWithGoogle(
            @Valid @RequestBody RegisterLesseeGoogleDto requestDto
    ) {
        AuthResponseDto authResponseDto = lesseeService.registerWithGoogleAccount(requestDto);

        return new BaseResponse<>(
                true, authResponseDto, "Arrendatario registrado y logueado exitosamente", HttpStatus.CREATED
        ).buildResponseEntity();
    }

    @Operation(
            summary = "Paso 1: Solicitar desafío de registro biométrico",
            description = """
                    Genera un challenge WebAuthn para iniciar el registro de credencial biométrica (huella digital).

                    El campo `data` contiene un objeto `PublicKeyCredentialCreationOptions` serializado con la siguiente estructura:
                    - `publicKey.rp`: información del Relying Party (nombre e identificador de dominio de la API).
                    - `publicKey.user.name`: correo electrónico del usuario.
                    - `publicKey.user.displayName`: nombre completo del usuario.
                    - `publicKey.user.id`: identificador único del usuario codificado en Base64URL.
                    - `publicKey.challenge`: cadena Base64URL única y de un solo uso para prevenir ataques de repetición.
                    - `publicKey.pubKeyCredParams`: lista de algoritmos criptográficos aceptados (ej. ES256 = -7, RS256 = -257).
                    - `publicKey.excludeCredentials`: lista de credenciales ya registradas para evitar duplicados.
                    - `publicKey.attestation`: modo de atestación ("none" por compatibilidad con dispositivos móviles).
                    - `publicKey.extensions.credProps`: solicita al autenticador que reporte si la clave es residente (passkey).
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Challenge WebAuthn de registro generado exitosamente.",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "Respuesta exitosa",
                            value = """
                                    {
                                      "success": true,
                                      "data": {
                                        "publicKey": {
                                          "rp": {
                                            "name": "ViviaAplication",
                                            "id": "vivia-api.aleosh.online"
                                          },
                                          "user": {
                                            "name": "luis@gmail.com",
                                            "displayName": "Luis Guzmán",
                                            "id": "ZjA4YTMxZjMtZjUzMC00NmRjLWI5MDQtNTEyN2JiMmZjNGIy"
                                          },
                                          "challenge": "y6c8iTMikxA_RpMmjNPxZMg4ag4dLM_Xda7AfXjJNNs",
                                          "pubKeyCredParams": [
                                            { "alg": -7,   "type": "public-key" },
                                            { "alg": -8,   "type": "public-key" },
                                            { "alg": -35,  "type": "public-key" },
                                            { "alg": -36,  "type": "public-key" },
                                            { "alg": -257, "type": "public-key" },
                                            { "alg": -258, "type": "public-key" },
                                            { "alg": -259, "type": "public-key" }
                                          ],
                                          "excludeCredentials": [],
                                          "attestation": "none",
                                          "extensions": {
                                            "credProps": true
                                          }
                                        }
                                      },
                                      "message": "Challenge de registro biométrico generado",
                                      "status": "OK"
                                    }
                                    """
                    )
            )
    )
    @PostMapping(value = "/biometric/challenge", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BaseResponse<String>> startBiometricRegistration(
            @Valid @RequestBody RegisterLesseeBiometricChallengeDto requestDto
    ) {
        String challengeJson = lesseeService.startBiometricRegistration(requestDto);

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
            @Valid @RequestBody RegisterLesseeBiometricVerifyDto requestDto
    ) {
        AuthResponseDto authResponseDto = lesseeService.finishBiometricRegistration(requestDto);

        return new BaseResponse<>(
                true, authResponseDto, "Arrendatario registrado y logueado exitosamente", HttpStatus.CREATED
        ).buildResponseEntity();
    }

}