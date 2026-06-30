package aleosh.online.vivia.features.users.users.controllers;

import aleosh.online.vivia.core.config.security.CustomUserDetails;
import aleosh.online.vivia.core.dtos.BaseResponse;
import aleosh.online.vivia.features.users.users.data.dtos.response.UserMeResponseDto;
import aleosh.online.vivia.features.users.users.data.dtos.response.UserProfileResponseDto;
import aleosh.online.vivia.features.users.users.domain.entities.User;
import aleosh.online.vivia.features.users.users.services.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Tag(name = "User", description = "Perfil del usuario autenticado")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Perfil mínimo", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/me")
    public ResponseEntity<BaseResponse<UserMeResponseDto>> getMe(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userService.getMe(userDetails.getUserId());

        UserMeResponseDto dto = new UserMeResponseDto(user.getId(), user.getName(), user.getPhotoUrl());

        return new BaseResponse<>(true, dto, "Perfil obtenido", HttpStatus.OK).buildResponseEntity();
    }

    @Operation(summary = "Perfil completo", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/profile")
    public ResponseEntity<BaseResponse<UserProfileResponseDto>> getProfile(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserProfileResponseDto dto = userService.getProfile(userDetails.getUserId());

        return new BaseResponse<>(true, dto, "Perfil obtenido", HttpStatus.OK).buildResponseEntity();
    }
}
