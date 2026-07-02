package aleosh.online.vivia.features.reports.data.dtos.response;

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
public class PropertyReportSummaryDto {
    private UUID id;
    private String propertyTitle;
    private String lessorName;
    private ReportReasonDto reason;
    private String comment;
    private boolean isResolved;
    private String verdict;
    private Instant createdAt;
}
