package aleosh.online.vivia.features.users.users.services;

import aleosh.online.vivia.features.users.users.data.dtos.request.PhotoPresignRequestDto;
import aleosh.online.vivia.features.users.users.data.dtos.response.PhotoPresignResponseDto;

import java.util.UUID;

public interface IPhotoPresignService {
    PhotoPresignResponseDto generateUploadUrl(UUID userId, PhotoPresignRequestDto dto);
}
