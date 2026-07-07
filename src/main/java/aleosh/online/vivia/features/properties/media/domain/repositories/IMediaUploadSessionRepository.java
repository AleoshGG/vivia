package aleosh.online.vivia.features.properties.media.domain.repositories;

import aleosh.online.vivia.features.properties.media.domain.entities.MediaUploadSession;

import java.util.Optional;
import java.util.UUID;

public interface IMediaUploadSessionRepository {

    MediaUploadSession save(MediaUploadSession session);

    Optional<MediaUploadSession> getById(UUID id);

    void deleteById(UUID id);

    void updateStatus(UUID sessionId, String newStatus);

    int incrementUploadedFiles(UUID sessionId);

    int getUploadedFilesCount(UUID sessionId);
}
