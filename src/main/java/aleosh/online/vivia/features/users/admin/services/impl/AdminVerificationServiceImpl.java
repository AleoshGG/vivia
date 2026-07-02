package aleosh.online.vivia.features.users.admin.services.impl;

import aleosh.online.vivia.features.properties.draft.messaging.events.NotificationEvent;
import aleosh.online.vivia.features.properties.draft.messaging.publishers.NotificationPublisher;
import aleosh.online.vivia.features.users.admin.data.dtos.request.AdminUpdateVerificationRequestDto;
import aleosh.online.vivia.features.users.admin.data.dtos.response.LessorVerificationDetailDto;
import aleosh.online.vivia.features.users.admin.data.dtos.response.LessorVerificationSummaryDto;
import aleosh.online.vivia.features.users.admin.data.mappers.AdminVerificationMapper;
import aleosh.online.vivia.features.users.admin.services.IAdminVerificationService;
import aleosh.online.vivia.features.users.lessor.data.entities.LessorDocumentEntity;
import aleosh.online.vivia.features.users.lessor.data.entities.LessorEntity;
import aleosh.online.vivia.features.users.lessor.data.entities.VerificationRejectionEntity;
import aleosh.online.vivia.features.users.lessor.data.repositories.LessorDocumentRepository;
import aleosh.online.vivia.features.users.lessor.data.repositories.LessorRepository;
import aleosh.online.vivia.features.users.lessor.data.repositories.VerificationRejectionRepository;
import aleosh.online.vivia.features.users.lessor.domain.exceptions.LessorNotFoundException;
import aleosh.online.vivia.features.users.lessor.domain.objectvalues.VerificationStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminVerificationServiceImpl implements IAdminVerificationService {

    private final LessorRepository lessorRepository;
    private final LessorDocumentRepository lessorDocumentRepository;
    private final VerificationRejectionRepository verificationRejectionRepository;
    private final AdminVerificationMapper adminVerificationMapper;
    private final NotificationPublisher notificationPublisher;

    public AdminVerificationServiceImpl(
            LessorRepository lessorRepository,
            LessorDocumentRepository lessorDocumentRepository,
            VerificationRejectionRepository verificationRejectionRepository,
            AdminVerificationMapper adminVerificationMapper,
            NotificationPublisher notificationPublisher
    ) {
        this.lessorRepository = lessorRepository;
        this.lessorDocumentRepository = lessorDocumentRepository;
        this.verificationRejectionRepository = verificationRejectionRepository;
        this.adminVerificationMapper = adminVerificationMapper;
        this.notificationPublisher = notificationPublisher;
    }

    @Override
    public List<LessorVerificationSummaryDto> getLessorsByStatus(VerificationStatus status) {
        return lessorRepository.findByVerificationStatus(status).stream()
                .map(lessor -> adminVerificationMapper.toSummaryDto(
                        lessor,
                        lessorDocumentRepository.findLatestUploadedAtByLessorId(lessor.getId()).orElse(null)
                ))
                .collect(Collectors.toList());
    }

    @Override
    public LessorVerificationDetailDto getLessorDocuments(UUID lessorId) {
        LessorEntity lessor = lessorRepository.findById(lessorId)
                .orElseThrow(() -> new LessorNotFoundException("Lessor " + lessorId + " no encontrado."));
        List<LessorDocumentEntity> documents = lessorDocumentRepository.findByLessorId(lessorId);
        return adminVerificationMapper.toDetailDto(lessor, documents);
    }

    @Override
    @Transactional
    public void updateVerificationStatus(UUID lessorId, AdminUpdateVerificationRequestDto dto) {
        LessorEntity lessor = lessorRepository.findById(lessorId)
                .orElseThrow(() -> new LessorNotFoundException("Lessor " + lessorId + " no encontrado."));

        VerificationStatus newStatus = VerificationStatus.valueOf(dto.getVerificationStatus());

        verificationRejectionRepository.findByLessor_Id(lessorId)
                .ifPresent(verificationRejectionRepository::delete);

        if (newStatus == VerificationStatus.REJECTED) {
            VerificationRejectionEntity rejection = VerificationRejectionEntity.builder()
                    .id(UUID.randomUUID())
                    .lessor(lessor)
                    .comment(dto.getComment())
                    .reasons(dto.getReasons() != null ? dto.getReasons() : new ArrayList<>())
                    .build();
            verificationRejectionRepository.save(rejection);
        }

        lessorRepository.updateVerificationStatus(lessorId, newStatus);

        if (newStatus == VerificationStatus.VERIFIED) {
            notificationPublisher.publish(new NotificationEvent(
                    lessorId,
                    "¡Verificación aprobada!",
                    "Tu identidad fue verificada. Ya puedes publicar propiedades.",
                    Map.of("type", "VERIFICATION_STATUS_UPDATED", "status", "VERIFIED")
            ));
        } else if (newStatus == VerificationStatus.REJECTED) {
            notificationPublisher.publish(new NotificationEvent(
                    lessorId,
                    "Verificación rechazada",
                    "Tu identidad no pudo verificarse. Revisa los comentarios del administrador.",
                    Map.of("type", "VERIFICATION_STATUS_UPDATED", "status", "REJECTED")
            ));
        }
    }
}
