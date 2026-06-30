package aleosh.online.vivia.features.users.lessor.data.mappers;

import aleosh.online.vivia.features.users.lessor.data.entities.LessorEntity;
import aleosh.online.vivia.features.users.lessor.domain.entities.Lessor;
import org.springframework.stereotype.Component;

@Component("lessorDataMapper")
public class LessorMapper {
    public Lessor toDomain(LessorEntity lessorEntity) {
        if (lessorEntity == null) { return null; }

        return Lessor.builder()
                .id(lessorEntity.getId())
                .phoneNumber(lessorEntity.getPhoneNumber())
                .verificationStatus(lessorEntity.getVerificationStatus())
                .build();
    }

    public LessorEntity toEntity(Lessor lessor) {
        if (lessor == null) { return null; }

        LessorEntity lessorEntity = new LessorEntity();
        lessorEntity.setId(lessor.getId());
        lessorEntity.setPhoneNumber(lessor.getPhoneNumber());
        lessorEntity.setVerificationStatus(lessor.getVerificationStatus());

        return lessorEntity;
    }
}
