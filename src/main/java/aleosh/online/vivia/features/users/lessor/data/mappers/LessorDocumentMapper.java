package aleosh.online.vivia.features.users.lessor.data.mappers;

import aleosh.online.vivia.features.users.lessor.data.entities.LessorDocumentEntity;
import aleosh.online.vivia.features.users.lessor.data.entities.LessorEntity;
import aleosh.online.vivia.features.users.lessor.domain.entities.LessorDocument;
import org.springframework.stereotype.Component;

@Component("lessorDocumentDataMapper")
public class LessorDocumentMapper {

    public LessorDocument toDomain(LessorDocumentEntity entity) {
        if (entity == null) { return null; }

        return LessorDocument.builder()
                .id(entity.getId())
                .lessorId(entity.getLessor().getId())
                .documentType(entity.getDocumentType())
                .uri(entity.getUri())
                .uploadedAt(entity.getUploadedAt())
                .build();
    }

    public LessorDocumentEntity toEntity(LessorDocument document, LessorEntity lessorEntity) {
        if (document == null) { return null; }

        return LessorDocumentEntity.builder()
                .id(document.getId())
                .lessor(lessorEntity)
                .documentType(document.getDocumentType())
                .uri(document.getUri())
                .build();
    }
}
