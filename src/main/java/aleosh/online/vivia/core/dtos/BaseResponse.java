package aleosh.online.vivia.core.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {
    private Boolean success;
    private T data;
    private String message;
    private HttpStatus status;

    public ResponseEntity<BaseResponse<T>> buildResponseEntity() {
        return new ResponseEntity<>(this, this.status);
    }
}
