package aleosh.online.vivia.features.reports.services.impl;

import aleosh.online.vivia.features.properties.draft.messaging.events.NotificationEvent;
import aleosh.online.vivia.features.properties.draft.messaging.publishers.NotificationPublisher;
import aleosh.online.vivia.features.properties.properties.data.entities.PropertyEntity;
import aleosh.online.vivia.features.properties.properties.data.repositories.PropertyRepository;
import aleosh.online.vivia.features.reports.controllers.ReportSsePublisher;
import aleosh.online.vivia.features.reports.data.dtos.request.CreateReportRequestDto;
import aleosh.online.vivia.features.reports.data.dtos.request.ReportVerdictRequestDto;
import aleosh.online.vivia.features.reports.data.dtos.response.PropertyReportDetailDto;
import aleosh.online.vivia.features.reports.data.dtos.response.PropertyReportSummaryDto;
import aleosh.online.vivia.features.reports.data.entities.PropertyReportEntity;
import aleosh.online.vivia.features.reports.data.entities.ReportReasonEntity;
import aleosh.online.vivia.features.reports.data.repositories.PropertyReportRepository;
import aleosh.online.vivia.features.reports.data.repositories.ReportReasonRepository;
import aleosh.online.vivia.features.reports.domain.exceptions.ReportAlreadyExistsException;
import aleosh.online.vivia.features.reports.domain.objectvalues.ReportVerdict;
import aleosh.online.vivia.features.reports.services.IReportService;
import aleosh.online.vivia.features.reports.services.mappers.ReportMapper;
import aleosh.online.vivia.features.users.lessee.data.entities.LesseeEntity;
import aleosh.online.vivia.features.users.lessee.data.repositories.LesseeRepository;
import aleosh.online.vivia.features.users.lessor.data.entities.LessorEntity;
import aleosh.online.vivia.features.users.lessor.data.repositories.LessorRepository;
import aleosh.online.vivia.features.users.users.data.entities.UserEntity;
import aleosh.online.vivia.features.users.users.data.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements IReportService {

    private final PropertyReportRepository reportRepository;
    private final ReportReasonRepository reasonRepository;
    private final PropertyRepository propertyRepository;
    private final LesseeRepository lesseeRepository;
    private final LessorRepository lessorRepository;
    private final UserRepository userRepository;
    private final ReportMapper reportMapper;
    private final NotificationPublisher notificationPublisher;
    private final ReportSsePublisher reportSsePublisher;

    public ReportServiceImpl(
            PropertyReportRepository reportRepository,
            ReportReasonRepository reasonRepository,
            PropertyRepository propertyRepository,
            LesseeRepository lesseeRepository,
            LessorRepository lessorRepository,
            UserRepository userRepository,
            ReportMapper reportMapper,
            NotificationPublisher notificationPublisher,
            ReportSsePublisher reportSsePublisher
    ) {
        this.reportRepository = reportRepository;
        this.reasonRepository = reasonRepository;
        this.propertyRepository = propertyRepository;
        this.lesseeRepository = lesseeRepository;
        this.lessorRepository = lessorRepository;
        this.userRepository = userRepository;
        this.reportMapper = reportMapper;
        this.notificationPublisher = notificationPublisher;
        this.reportSsePublisher = reportSsePublisher;
    }

    @Override
    @Transactional
    public void createReport(UUID lesseeId, CreateReportRequestDto dto) {
        PropertyEntity property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new EntityNotFoundException("Propiedad no encontrada"));

        if (property.getDeletedAt() != null) {
            throw new EntityNotFoundException("La propiedad no está disponible");
        }

        LessorEntity lessor = property.getLessor();
        if (lessor != null && lessor.getId().equals(lesseeId)) {
            throw new IllegalArgumentException("No puedes reportar tu propia propiedad");
        }

        if (reportRepository.existsByPropertyIdAndLesseeId(dto.getPropertyId(), lesseeId)) {
            throw new ReportAlreadyExistsException("Ya existe un reporte para esta propiedad");
        }

        LesseeEntity lessee = lesseeRepository.findById(lesseeId)
                .orElseThrow(() -> new EntityNotFoundException("Arrendatario no encontrado"));

        ReportReasonEntity reason = reasonRepository.findById(dto.getReasonId())
                .orElseThrow(() -> new EntityNotFoundException("Razón de reporte no encontrada"));

        PropertyReportEntity report = PropertyReportEntity.builder()
                .id(UUID.randomUUID())
                .property(property)
                .propertyTitle(property.getTitle())
                .lessor(lessor)
                .lessee(lessee)
                .reason(reason)
                .comment(dto.getComment())
                .build();

        reportRepository.save(report);
        reportSsePublisher.publish(report);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyReportSummaryDto> getPendingReports() {
        return reportRepository.findAllPending().stream()
                .map(reportMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PropertyReportDetailDto getReportDetail(UUID reportId) {
        PropertyReportEntity report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Reporte no encontrado"));
        return reportMapper.toDetailDto(report);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyReportSummaryDto> getHistoryByLessor(UUID lessorId) {
        return reportRepository.findAllByLessorIdOrderByCreatedAtDesc(lessorId).stream()
                .map(reportMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void applyVerdict(UUID reportId, UUID adminId, ReportVerdictRequestDto dto) {
        PropertyReportEntity report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Reporte no encontrado"));

        ReportVerdict verdict = ReportVerdict.valueOf(dto.getVerdict());
        UserEntity admin = userRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Administrador no encontrado"));

        report.setVerdict(verdict.name());
        report.setResolved(true);
        report.setResolvedBy(admin);
        report.setResolvedAt(Instant.now());
        reportRepository.save(report);

        LessorEntity lessor = report.getLessor();

        if (verdict == ReportVerdict.PROPERTY_DELETED && report.getProperty() != null) {
            PropertyEntity property = report.getProperty();
            property.setDeletedAt(Instant.now());
            propertyRepository.save(property);
            notifyLessor(lessor, "Una publicación tuya fue eliminada",
                    "Una de tus publicaciones fue eliminada por incumplir las reglas de la comunidad.");
        } else if (verdict == ReportVerdict.ACCOUNT_SUSPENDED && lessor != null) {
            lessor.setAccountStatus("SUSPENDED");
            lessorRepository.save(lessor);
            notifyLessor(lessor, "Cuenta suspendida",
                    "Tu cuenta ha sido suspendida. Contacta a soporte para más información.");
        }
    }

    private void notifyLessor(LessorEntity lessor, String title, String body) {
        if (lessor == null || lessor.getUser() == null) {
            return;
        }
        UUID lessorUserId = lessor.getUser().getId();
        notificationPublisher.publish(new NotificationEvent(lessorUserId, title, body, Map.of()));
    }
}
