package aleosh.online.vivia.features.users.lessor.services;

import aleosh.online.vivia.features.users.lessor.data.dtos.request.VerificationUploadRequestDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.response.VerificationUploadResponseDto;

import java.util.UUID;

public interface IVerificationPresignService {
    VerificationUploadResponseDto generateUploadUrls(UUID lessorId, VerificationUploadRequestDto dto);
}
