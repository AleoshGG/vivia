package aleosh.online.vivia.features.properties.likes;

import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyPreviewResponseDto;

import java.util.List;
import java.util.UUID;

public interface IPropertyLikeService {
    boolean toggleLike(UUID userId, UUID propertyId);
    List<PropertyPreviewResponseDto> getMyLikes(UUID userId);
}
