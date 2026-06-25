package aleosh.online.vivia.features.users.users.data.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMeResponseDto {
    private UUID id;
    private String name;
    private String photoUrl;
}
