package aleosh.online.vivia.features.users.admin.controllers;

import aleosh.online.vivia.core.config.messaging.RedisPubSubConfig;
import aleosh.online.vivia.features.users.admin.data.dtos.response.LessorVerificationSummaryDto;
import aleosh.online.vivia.features.users.admin.data.mappers.AdminVerificationMapper;
import aleosh.online.vivia.features.users.lessor.data.entities.LessorEntity;
import aleosh.online.vivia.features.users.lessor.data.repositories.LessorDocumentRepository;
import aleosh.online.vivia.features.users.lessor.data.repositories.LessorRepository;
import aleosh.online.vivia.features.users.lessor.domain.exceptions.LessorNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class VerificationSsePublisher {

    private static final Logger log = LoggerFactory.getLogger(VerificationSsePublisher.class);

    private final LessorRepository lessorRepository;
    private final LessorDocumentRepository lessorDocumentRepository;
    private final AdminVerificationMapper adminVerificationMapper;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public VerificationSsePublisher(
            LessorRepository lessorRepository,
            LessorDocumentRepository lessorDocumentRepository,
            AdminVerificationMapper adminVerificationMapper,
            StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper
    ) {
        this.lessorRepository = lessorRepository;
        this.lessorDocumentRepository = lessorDocumentRepository;
        this.adminVerificationMapper = adminVerificationMapper;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public void publish(UUID lessorId) {
        LessorEntity lessor = lessorRepository.findById(lessorId)
                .orElseThrow(() -> new LessorNotFoundException("Lessor " + lessorId + " no encontrado."));

        OffsetDateTime lastUploadedAt = lessorDocumentRepository
                .findLatestUploadedAtByLessorId(lessorId)
                .orElse(null);

        LessorVerificationSummaryDto dto = adminVerificationMapper.toSummaryDto(lessor, lastUploadedAt);

        try {
            String data = objectMapper.writeValueAsString(dto);
            String envelope = String.format("{\"event\":\"verification_pending\",\"data\":%s}", data);
            redisTemplate.convertAndSend(RedisPubSubConfig.VERIFICATION_PENDING_CHANNEL, envelope);
        } catch (JsonProcessingException e) {
            log.error("Error serializando evento SSE de verificación para lessorId={}", lessorId, e);
        }
    }
}
