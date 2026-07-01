package aleosh.online.vivia.features.users.admin.services.impl;

import aleosh.online.vivia.features.users.admin.data.dtos.request.AdminUpdateVerificationRequestDto;
import aleosh.online.vivia.features.users.admin.data.dtos.response.LessorDocumentResponseDto;
import aleosh.online.vivia.features.users.admin.data.dtos.response.LessorVerificationSummaryDto;
import aleosh.online.vivia.features.users.admin.services.IAdminVerificationService;
import aleosh.online.vivia.features.users.lessor.data.repositories.LessorDocumentRepository;
import aleosh.online.vivia.features.users.lessor.data.repositories.LessorRepository;
import aleosh.online.vivia.features.users.lessor.domain.exceptions.LessorNotFoundException;
import aleosh.online.vivia.features.users.lessor.domain.objectvalues.VerificationStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminVerificationServiceImpl implements IAdminVerificationService {

    private final LessorRepository lessorRepository;
    private final LessorDocumentRepository lessorDocumentRepository;

    public AdminVerificationServiceImpl(
            LessorRepository lessorRepository,
            LessorDocumentRepository lessorDocumentRepository
    ) {
        this.lessorRepository = lessorRepository;
        this.lessorDocumentRepository = lessorDocumentRepository;
    }

    @Override
    public List<LessorVerificationSummaryDto> getLessorsByStatus(VerificationStatus status) {
        return lessorRepository.findByVerificationStatus(status).stream()
                .map(lessor -> new LessorVerificationSummaryDto(
                        lessor.getId(),
                        lessor.getUser().getName() + " " + lessor.getUser().getPaternalSurname(),
                        lessor.getUser().getEmail(),
                        lessor.getVerificationStatus().name()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<LessorDocumentResponseDto> getLessorDocuments(UUID lessorId) {
        if (!lessorRepository.existsById(lessorId)) {
            throw new LessorNotFoundException("Lessor " + lessorId + " no encontrado.");
        }
        return lessorDocumentRepository.findByLessorId(lessorId).stream()
                .map(doc -> new LessorDocumentResponseDto(
                        doc.getId(),
                        doc.getDocumentType().name(),
                        doc.getUri(),
                        doc.getUploadedAt()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateVerificationStatus(UUID lessorId, AdminUpdateVerificationRequestDto dto) {
        if (!lessorRepository.existsById(lessorId)) {
            throw new LessorNotFoundException("Lessor " + lessorId + " no encontrado.");
        }
        VerificationStatus newStatus = VerificationStatus.valueOf(dto.getVerificationStatus());
        lessorRepository.updateVerificationStatus(lessorId, newStatus);
    }
}
