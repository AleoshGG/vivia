package aleosh.online.vivia.features.users.lessee.data.mappers;

import aleosh.online.vivia.features.users.lessee.data.entities.LesseeEntity;
import aleosh.online.vivia.features.users.lessee.domain.entities.Lessee;
import aleosh.online.vivia.features.users.lessor.data.entities.LessorEntity;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.UUID;

@Component("lesseeDataMapper")
public class LesseeMapper {
    public Lessee toDomain(LesseeEntity lesseeEntity) {
        if (lesseeEntity == null) { return null; }

        Lessee.Builder builder = Lessee.builder()
                .id(lesseeEntity.getId())
                .latitude(lesseeEntity.getLatitude() != null ? lesseeEntity.getLatitude().doubleValue() : null)
                .longitude(lesseeEntity.getLongitude() != null ? lesseeEntity.getLongitude().doubleValue() : null);

        return builder.build();
    }

    public LesseeEntity toEntity(Lessee lessee) {
        if (lessee == null) { return null; }

        LesseeEntity lesseeEntity = new LesseeEntity();
        lesseeEntity.setId(lessee.getId());
        lesseeEntity.setLatitude(lessee.getLatitude() != null ? java.math.BigDecimal.valueOf(lessee.getLatitude()) : null);
        lesseeEntity.setLongitude(lessee.getLongitude() != null ? java.math.BigDecimal.valueOf(lessee.getLongitude()) : null);

        return lesseeEntity;
    }
}