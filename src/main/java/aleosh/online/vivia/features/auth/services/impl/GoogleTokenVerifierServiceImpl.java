package aleosh.online.vivia.features.auth.services.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

import aleosh.online.vivia.features.auth.domain.exceptions.AuthException;
import aleosh.online.vivia.features.auth.domain.exceptions.InvalidTokenException;
import aleosh.online.vivia.core.exceptions.DomainException;
import org.springframework.http.HttpStatus;

@Service
public class GoogleTokenVerifierServiceImpl {

    @Value("${google.client-id}")
    private String googleClientId;

    public GoogleIdToken.Payload verifyIdToken(String idToken) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance()
            )
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken googleIdToken = verifier.verify(idToken);

            if (googleIdToken == null) {
                throw new InvalidTokenException("ID Token inválido");
            }

            return googleIdToken.getPayload();

        } catch (DomainException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthException("Error al verificar el ID Token de Google: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
