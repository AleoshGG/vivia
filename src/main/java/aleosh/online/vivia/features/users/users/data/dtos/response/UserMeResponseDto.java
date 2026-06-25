package aleosh.online.vivia.features.users.users.data.dtos.response;

import java.util.UUID;

public record UserMeResponseDto(
        UUID id,
        String name,
        String photoUrl
) {}
