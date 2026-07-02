package aleosh.online.vivia.features.users.admin.data.mappers;

import aleosh.online.vivia.features.users.admin.data.dtos.response.LessorDocumentResponseDto;
import aleosh.online.vivia.features.users.admin.data.dtos.response.LessorVerificationDetailDto;
import aleosh.online.vivia.features.users.admin.data.dtos.response.LessorVerificationSummaryDto;
import aleosh.online.vivia.features.users.lessor.data.entities.LessorDocumentEntity;
import aleosh.online.vivia.features.users.lessor.data.entities.LessorEntity;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AdminVerificationMapper {

    public LessorVerificationSummaryDto toSummaryDto(LessorEntity lessor, OffsetDateTime lastUploadedAt) {
        return new LessorVerificationSummaryDto(
                lessor.getId(),
                lessor.getUser().getName() + " " + lessor.getUser().getPaternalSurname(),
                lessor.getUser().getEmail(),
                lessor.getVerificationStatus().name(),
                lastUploadedAt
        );
    }

    public LessorVerificationDetailDto toDetailDto(LessorEntity lessor, List<LessorDocumentEntity> documents) {
        List<LessorDocumentResponseDto> documentDtos = documents.stream()
                .map(this::toDocumentDto)
                .collect(Collectors.toList());

        return new LessorVerificationDetailDto(
                lessor.getId(),
                lessor.getUser().getName(),
                lessor.getUser().getPaternalSurname(),
                lessor.getUser().getMaternalSurname(),
                lessor.getUser().getEmail(),
                lessor.getUser().getPhotoUrl(),
                lessor.getPhoneNumber(),
                lessor.getVerificationStatus().name(),
                lessor.getUser().getCreatedAt(),
                lessor.getUser().getUpdatedAt(),
                documentDtos
        );
    }

    public LessorDocumentResponseDto toDocumentDto(LessorDocumentEntity doc) {
        return new LessorDocumentResponseDto(
                doc.getId(),
                doc.getDocumentType().name(),
                doc.getUri(),
                doc.getUploadedAt()
        );
    }
}
