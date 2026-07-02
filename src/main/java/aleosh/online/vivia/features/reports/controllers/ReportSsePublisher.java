package aleosh.online.vivia.features.reports.controllers;

import aleosh.online.vivia.core.config.messaging.RedisPubSubConfig;
import aleosh.online.vivia.features.reports.data.dtos.response.PropertyReportSummaryDto;
import aleosh.online.vivia.features.reports.data.entities.PropertyReportEntity;
import aleosh.online.vivia.features.reports.services.mappers.ReportMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class ReportSsePublisher {

    private static final Logger log = LoggerFactory.getLogger(ReportSsePublisher.class);

    private final ReportMapper reportMapper;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public ReportSsePublisher(ReportMapper reportMapper, StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.reportMapper = reportMapper;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void publish(PropertyReportEntity report) {
        PropertyReportSummaryDto dto = reportMapper.toSummaryDto(report);
        try {
            String data = objectMapper.writeValueAsString(dto);
            String envelope = String.format("{\"event\":\"report_new\",\"data\":%s}", data);
            redisTemplate.convertAndSend(RedisPubSubConfig.REPORTS_NEW_CHANNEL, envelope);
        } catch (JsonProcessingException e) {
            log.error("Error serializando evento SSE de reporte id={}", report.getId(), e);
        }
    }
}
