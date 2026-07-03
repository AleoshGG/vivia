package aleosh.online.vivia.core.config.messaging;

import aleosh.online.vivia.features.properties.draft.controllers.DraftStatusMessageListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisPubSubConfig {

    public static final String DRAFT_STATUS_CHANNEL_PATTERN = "draft:status:*";
    public static final String DRAFT_STATUS_CHANNEL_PREFIX = "draft:status:";

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            DraftStatusMessageListener draftStatusMessageListener
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(draftStatusMessageListener,
                new PatternTopic(DRAFT_STATUS_CHANNEL_PATTERN));
        return container;
    }
}
