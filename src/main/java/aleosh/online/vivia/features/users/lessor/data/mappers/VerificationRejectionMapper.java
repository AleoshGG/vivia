package aleosh.online.vivia.features.users.lessor.data.mappers;

import aleosh.online.vivia.features.users.lessor.data.entities.VerificationRejectionEntity;
import aleosh.online.vivia.features.users.lessor.domain.entities.VerificationRejection;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class VerificationRejectionMapper {

    public VerificationRejection toDomain(VerificationRejectionEntity entity) {
        if (entity == null) { return null; }
        return VerificationRejection.builder()
                .id(entity.getId())
                .lessorId(entity.getLessor().getId())
                .comment(entity.getComment())
                .reasons(entity.getReasons() != null ? entity.getReasons() : new ArrayList<>())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
