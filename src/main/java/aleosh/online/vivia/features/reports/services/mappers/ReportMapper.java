package aleosh.online.vivia.features.reports.services.mappers;

import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyDetailResponseDto;
import aleosh.online.vivia.features.properties.properties.services.mappers.PropertyDetailMapper;
import aleosh.online.vivia.features.reports.data.dtos.response.PropertyReportDetailDto;
import aleosh.online.vivia.features.reports.data.dtos.response.PropertyReportSummaryDto;
import aleosh.online.vivia.features.reports.data.dtos.response.ReportLesseeDto;
import aleosh.online.vivia.features.reports.data.dtos.response.ReportLessorDto;
import aleosh.online.vivia.features.reports.data.dtos.response.ReportReasonDto;
import aleosh.online.vivia.features.reports.data.entities.PropertyReportEntity;
import aleosh.online.vivia.features.users.lessor.data.entities.LessorEntity;
import aleosh.online.vivia.features.users.users.data.entities.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class ReportMapper {

    private final PropertyDetailMapper propertyDetailMapper;

    public ReportMapper(PropertyDetailMapper propertyDetailMapper) {
        this.propertyDetailMapper = propertyDetailMapper;
    }

    public PropertyReportSummaryDto toSummaryDto(PropertyReportEntity entity) {
        LessorEntity lessor = entity.getLessor();
        UserEntity lessorUser = lessor != null ? lessor.getUser() : null;
        String lessorName = lessorUser != null
                ? lessorUser.getName() + " " + lessorUser.getPaternalSurname()
                : "";

        ReportReasonDto reasonDto = entity.getReason() != null
                ? ReportReasonDto.builder()
                    .name(entity.getReason().getName())
                    .priority(entity.getReason().getPriority())
                    .build()
                : null;

        return PropertyReportSummaryDto.builder()
                .id(entity.getId())
                .propertyTitle(entity.getPropertyTitle())
                .lessorName(lessorName)
                .reason(reasonDto)
                .comment(entity.getComment())
                .isResolved(entity.isResolved())
                .verdict(entity.getVerdict())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public PropertyReportDetailDto toDetailDto(PropertyReportEntity entity) {
        PropertyDetailResponseDto propertyDto = null;
        if (entity.getProperty() != null) {
            propertyDto = propertyDetailMapper.toDetailResponse(entity.getProperty(), false, false);
        }

        LessorEntity lessor = entity.getLessor();
        UserEntity lessorUser = lessor != null ? lessor.getUser() : null;
        ReportLessorDto lessorDto = null;
        if (lessor != null && lessorUser != null) {
            lessorDto = ReportLessorDto.builder()
                    .id(lessor.getId())
                    .name(lessorUser.getName() + " " + lessorUser.getPaternalSurname())
                    .email(lessorUser.getEmail())
                    .accountStatus(lessor.getAccountStatus())
                    .build();
        }

        ReportLesseeDto lesseeDto = null;
        if (entity.getLessee() != null && entity.getLessee().getUser() != null) {
            UserEntity lesseeUser = entity.getLessee().getUser();
            lesseeDto = ReportLesseeDto.builder()
                    .id(entity.getLessee().getId())
                    .name(lesseeUser.getName() + " " + lesseeUser.getPaternalSurname())
                    .email(lesseeUser.getEmail())
                    .build();
        }

        ReportReasonDto reasonDto = entity.getReason() != null
                ? ReportReasonDto.builder()
                    .name(entity.getReason().getName())
                    .priority(entity.getReason().getPriority())
                    .build()
                : null;

        return PropertyReportDetailDto.builder()
                .id(entity.getId())
                .property(propertyDto)
                .propertyTitle(entity.getPropertyTitle())
                .lessor(lessorDto)
                .lessee(lesseeDto)
                .reason(reasonDto)
                .comment(entity.getComment())
                .isResolved(entity.isResolved())
                .verdict(entity.getVerdict())
                .createdAt(entity.getCreatedAt())
                .resolvedAt(entity.getResolvedAt())
                .build();
    }
}
