package aleosh.online.vivia.features.reports.data.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReportVerdictRequestDto {

    @NotBlank
    private String verdict;
}
