package aleosh.online.vivia.features.properties.draft.controllers;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SseEmitterRegistry {

    private final Map<UUID, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public void register(UUID draftId, SseEmitter emitter) {
        emitters.computeIfAbsent(draftId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> remove(draftId, emitter));
        emitter.onTimeout(() -> remove(draftId, emitter));
        emitter.onError(e -> remove(draftId, emitter));
    }

    public void notifyStatusChange(UUID draftId, String statusPayload) {
        List<SseEmitter> draftEmitters = emitters.get(draftId);
        if (draftEmitters == null || draftEmitters.isEmpty()) {
            return;
        }

        List<SseEmitter> dead = new CopyOnWriteArrayList<>();
        for (SseEmitter emitter : draftEmitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("status_update")
                        .data(statusPayload));
            } catch (Exception e) {
                dead.add(emitter);
            }
        }
        draftEmitters.removeAll(dead);
    }

    private void remove(UUID draftId, SseEmitter emitter) {
        List<SseEmitter> draftEmitters = emitters.get(draftId);
        if (draftEmitters != null) {
            draftEmitters.remove(emitter);
            if (draftEmitters.isEmpty()) {
                emitters.remove(draftId);
            }
        }
    }
}
