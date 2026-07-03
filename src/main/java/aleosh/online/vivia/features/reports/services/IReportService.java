package aleosh.online.vivia.features.reports.services;

import aleosh.online.vivia.features.reports.data.dtos.request.CreateReportRequestDto;
import aleosh.online.vivia.features.reports.data.dtos.request.ReportVerdictRequestDto;
import aleosh.online.vivia.features.reports.data.dtos.response.PropertyReportDetailDto;
import aleosh.online.vivia.features.reports.data.dtos.response.PropertyReportSummaryDto;
import aleosh.online.vivia.features.reports.data.dtos.response.ReportReasonDto;

import java.util.List;
import java.util.UUID;

public interface IReportService {
    void createReport(UUID lesseeId, CreateReportRequestDto dto);
    List<PropertyReportSummaryDto> getPendingReports();
    PropertyReportDetailDto getReportDetail(UUID reportId);
    List<PropertyReportSummaryDto> getHistoryByLessor(UUID lessorId);
    void applyVerdict(UUID reportId, UUID adminId, ReportVerdictRequestDto dto);
    List<ReportReasonDto> getReasons();
}
