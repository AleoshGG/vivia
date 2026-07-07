package aleosh.online.vivia.features.users.users.data.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Perfil completo del usuario autenticado")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponseDto {

    @Schema(description = "Nombre", example = "Alexis")
    private String name;

    @Schema(description = "Apellido paterno", example = "Guzmán")
    private String paternalSurname;

    @Schema(description = "Apellido materno", example = "González")
    private String maternalSurname;

    @Schema(description = "Correo electrónico", example = "alexis@example.com")
    private String email;

    @Schema(description = "URL de foto de perfil", example = "https://res.cloudinary.com/vivia/image/upload/v1/perfil.jpg")
    private String photoUrl;

    @Schema(description = "Estado de verificación de identidad. null si el usuario es inquilino.", example = "UNVERIFIED", nullable = true, allowableValues = {"UNVERIFIED", "PENDING_REVIEW", "VERIFIED", "REJECTED"})
    private String verificationStatus;

    @Schema(description = "Número de teléfono del arrendador. null si el usuario es inquilino.", example = "5512345678", nullable = true)
    private String phoneNumber;
}
