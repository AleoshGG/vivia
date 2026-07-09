package aleosh.online.vivia.features.users.lessee.data.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LesseeUbicationResponseDto {
    private BigDecimal latitude;
    private BigDecimal longitude;
}
