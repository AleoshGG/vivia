package aleosh.online.vivia.features.reports.data.dtos.response;

import aleosh.online.vivia.features.properties.properties.data.dtos.response.PropertyDetailResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyReportDetailDto {
    private UUID id;
    private PropertyDetailResponseDto property;
    private String propertyTitle;
    private ReportLessorDto lessor;
    private ReportLesseeDto lessee;
    private ReportReasonDto reason;
    private String comment;
    private boolean isResolved;
    private String verdict;
    private Instant createdAt;
    private Instant resolvedAt;
}
