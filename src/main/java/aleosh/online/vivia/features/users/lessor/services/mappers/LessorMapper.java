package aleosh.online.vivia.features.users.lessor.services.mappers;

import aleosh.online.vivia.features.users.lessor.data.dtos.response.LessorResponseDto;
import aleosh.online.vivia.features.users.lessor.data.entities.LessorEntity;
import org.springframework.stereotype.Component;

@Component("lessorServiceMapper")
public class LessorMapper {
    public LessorResponseDto toLessorResponseDto(LessorEntity lessorEntity) {
        if (lessorEntity == null) { return null; }

        LessorResponseDto dto = new LessorResponseDto();
        dto.setId(lessorEntity.getId());
        dto.setPhoneNumber(lessorEntity.getPhoneNumber());
        dto.setVerificationStatus(lessorEntity.getVerificationStatus().name());
        return dto;
    }
}
