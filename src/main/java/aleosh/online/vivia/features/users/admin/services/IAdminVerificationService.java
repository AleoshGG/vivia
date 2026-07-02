package aleosh.online.vivia.features.users.admin.services;

import aleosh.online.vivia.features.users.admin.data.dtos.request.AdminUpdateVerificationRequestDto;
import aleosh.online.vivia.features.users.admin.data.dtos.response.LessorVerificationDetailDto;
import aleosh.online.vivia.features.users.admin.data.dtos.response.LessorVerificationSummaryDto;
import aleosh.online.vivia.features.users.lessor.domain.objectvalues.VerificationStatus;

import java.util.List;
import java.util.UUID;

public interface IAdminVerificationService {
    List<LessorVerificationSummaryDto> getLessorsByStatus(VerificationStatus status);
    LessorVerificationDetailDto getLessorDocuments(UUID lessorId);
    void updateVerificationStatus(UUID lessorId, AdminUpdateVerificationRequestDto dto);
    void notifyLessorStatusChanged(UUID lessorId, VerificationStatus status);
}
