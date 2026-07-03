package aleosh.online.vivia.features.properties.likes.services.impl;

import aleosh.online.vivia.features.properties.likes.IPropertyLikeService;
import aleosh.online.vivia.features.properties.likes.domain.entities.PropertyLike;
import aleosh.online.vivia.features.properties.likes.domain.repositories.IPropertyLikeRepository;
import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyPreviewResponseDto;
import aleosh.online.vivia.features.properties.properties.data.repositories.PropertyRepository;
import aleosh.online.vivia.features.properties.properties.services.mappers.PropertyMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PropertyLikeServiceImpl implements IPropertyLikeService {

    private final IPropertyLikeRepository likeRepository;
    private final PropertyRepository propertyRepository;
    private final PropertyMapper propertyMapper;

    public PropertyLikeServiceImpl(
            IPropertyLikeRepository likeRepository,
            PropertyRepository propertyRepository,
            @Qualifier("propertyServiceMapper") PropertyMapper propertyMapper
    ) {
        this.likeRepository = likeRepository;
        this.propertyRepository = propertyRepository;
        this.propertyMapper = propertyMapper;
    }

    @Override
    public boolean toggleLike(UUID userId, UUID propertyId) {
        if (likeRepository.existsByUserIdAndPropertyId(userId, propertyId)) {
            likeRepository.deleteByUserIdAndPropertyId(userId, propertyId);
            return false;
        }
        likeRepository.save(PropertyLike.builder()
                .userId(userId)
                .propertyId(propertyId)
                .build());
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyPreviewResponseDto> getMyLikes(UUID userId) {
        List<UUID> propertyIds = likeRepository.findAllByUserId(userId)
                .stream()
                .map(PropertyLike::getPropertyId)
                .collect(Collectors.toList());

        return propertyRepository.findAllByIdInAndDeletedAtIsNull(propertyIds)
                .stream()
                .map(propertyMapper::toPreviewDto)
                .collect(Collectors.toList());
    }
}
