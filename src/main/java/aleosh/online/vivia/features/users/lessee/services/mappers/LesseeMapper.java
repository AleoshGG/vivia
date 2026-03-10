package aleosh.online.vivia.features.users.lessee.services.mappers;

import aleosh.online.vivia.features.users.lessee.data.dtos.response.LesseeResponseDto;
import aleosh.online.vivia.features.users.lessee.data.entities.LesseeEntity;
import org.springframework.stereotype.Component;

@Component("lesseeServiceMapper")
public class LesseeMapper {
    public LesseeResponseDto toLesseeResponseDto(LesseeEntity lesseeEntity) {
        if (lesseeEntity == null) { return null; }

        LesseeResponseDto dto = new LesseeResponseDto();
        dto.setId(lesseeEntity.getId());
        dto.setUsername(lesseeEntity.getUsername());
        dto.setEmail(lesseeEntity.getEmail());
        return dto;
    }
}