package aleosh.online.vivia.features.auth.services.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Collections;

import aleosh.online.vivia.features.auth.domain.exceptions.AuthException;
import aleosh.online.vivia.features.auth.domain.exceptions.InvalidTokenException;
import aleosh.online.vivia.core.exceptions.DomainException;
import org.springframework.http.HttpStatus;

@Service
public class GoogleTokenVerifierServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(GoogleTokenVerifierServiceImpl.class);

    @Value("${google.client-id}")
    private String googleClientId;

    private GoogleIdTokenVerifier verifier;

    @PostConstruct
    public void init() {
        logger.info("Inicializando GoogleTokenVerifier con client-id: {}", googleClientId);
        this.verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance()
        )
                .setAudience(Collections.singletonList(googleClientId))
                .build();
    }

    public GoogleIdToken.Payload verifyIdToken(String idToken) {
        try {
            logger.debug("Verificando ID Token de Google...");

            GoogleIdToken googleIdToken = verifier.verify(idToken);

            if (googleIdToken == null) {
                logger.error("Token verification failed - verifier.verify() returned null. " +
                    "Posibles causas: token expirado, audience (aud) no coincide con client-id, o firma inválida.");
                throw new InvalidTokenException("ID Token inválido o expirado. Verifica que el client-id de la app coincida con el configurado en el servidor.");
            }

            GoogleIdToken.Payload payload = googleIdToken.getPayload();
            logger.debug("Token verificado exitosamente para email: {}", payload.getEmail());

            return payload;

        } catch (DomainException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al verificar ID Token de Google", e);
            throw new AuthException("Error al verificar el ID Token de Google: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
