package aleosh.online.vivia.core.config.firebase;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class AnyFirebaseEnabledCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment env = context.getEnvironment();
        boolean fcm = Boolean.parseBoolean(env.getProperty("fcm.enabled", "false"));
        boolean firestore = Boolean.parseBoolean(env.getProperty("firestore.enabled", "false"));
        return fcm || firestore;
    }
}
