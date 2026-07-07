package aleosh.online.vivia.features.properties.media.services;

import aleosh.online.vivia.features.properties.media.data.dtos.request.AddPropertyMediaDto;
import aleosh.online.vivia.features.properties.media.data.dtos.request.ChangeMainImageDto;
import aleosh.online.vivia.features.properties.media.data.dtos.response.MediaUploadSessionResponseDto;

import java.util.UUID;

public interface IPropertyMediaService {

    void deleteMedia(UUID mediaId, UUID lessorId);

    void changeMainImage(ChangeMainImageDto dto, UUID lessorId);

    MediaUploadSessionResponseDto addMedia(AddPropertyMediaDto dto, UUID lessorId);
}
