package aleosh.online.vivia.features.reports.data.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class CreateReportRequestDto {

    @NotNull
    private UUID propertyId;

    @NotBlank
    private String reasonId;

    private String comment;
}
