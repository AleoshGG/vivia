package aleosh.online.vivia.core.config.messaging;

import aleosh.online.vivia.features.properties.draft.controllers.DraftStatusMessageListener;
import aleosh.online.vivia.features.reports.controllers.ReportStatusMessageListener;
import aleosh.online.vivia.features.users.admin.controllers.VerificationStatusMessageListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisPubSubConfig {

    public static final String DRAFT_STATUS_CHANNEL_PATTERN = "draft:status:*";
    public static final String DRAFT_STATUS_CHANNEL_PREFIX = "draft:status:";
    public static final String VERIFICATION_PENDING_CHANNEL = "vivia.verification.pending_review";
    public static final String REPORTS_NEW_CHANNEL = "vivia.reports.new";

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            DraftStatusMessageListener draftStatusMessageListener,
            VerificationStatusMessageListener verificationStatusMessageListener,
            ReportStatusMessageListener reportStatusMessageListener
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(draftStatusMessageListener,
                new PatternTopic(DRAFT_STATUS_CHANNEL_PATTERN));
        container.addMessageListener(verificationStatusMessageListener,
                new ChannelTopic(VERIFICATION_PENDING_CHANNEL));
        container.addMessageListener(reportStatusMessageListener,
                new ChannelTopic(REPORTS_NEW_CHANNEL));
        return container;
    }
}
