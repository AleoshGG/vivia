package aleosh.online.vivia.features.users.lessee.data.mappers;

import aleosh.online.vivia.features.users.lessee.data.entities.LesseeEntity;
import aleosh.online.vivia.features.users.lessee.domain.entities.Lessee;
import org.springframework.stereotype.Component;

@Component("lesseeDataMapper")
public class LesseeMapper {
    public Lessee toDomain(LesseeEntity lesseeEntity) {
        if (lesseeEntity == null) { return null; }

        return Lessee.builder()
                .id(lesseeEntity.getId())
                .username(lesseeEntity.getUsername())
                .email(lesseeEntity.getEmail())
                .build();
    }

    public LesseeEntity toEntity(Lessee lessee) {
        if (lessee == null) { return null; }

        LesseeEntity lesseeEntity = new LesseeEntity();
        lesseeEntity.setId(lessee.getId());
        lesseeEntity.setUsername(lessee.getUsername());
        lesseeEntity.setEmail(lessee.getEmail());

        return lesseeEntity;
    }
}