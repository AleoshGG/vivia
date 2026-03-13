package aleosh.online.vivia.core.config.webauthn;

import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class WebAuthnConfig {

    @Bean
    public RelyingParty relyingParty(
            aleosh.online.vivia.core.security.webauthn.data.WebAuthnCredentialAdapter credentialAdapter
    ) {
        // 1. Definimos la identidad de nuestra app (Relying Party)
        RelyingPartyIdentity rpIdentity = RelyingPartyIdentity.builder()
                .id("vivia.aleosh.online") // IMPORTANTE: En producción, debe ser tu dominio real (ej. vivia.online)
                .name("ViviaAplication")
                .build();

        // 2. Construimos el RelyingParty inyectando el adaptador
        return RelyingParty.builder()
                .identity(rpIdentity)
                .credentialRepository(credentialAdapter)
                // Aquí definimos los orígenes permitidos (Web y hashes de la app móvil)
                .origins(Set.of(
                        "https://vivia.aleosh.online",
                        "http://localhost:8080",
                        "http://localhost:3000",
                        "android:apk-key-hash:usWotFl5QnpwvgXbGEq_ESBUUuZZ8NR7MmKWLVv1EKY",
                        "android:apk-key-hash:GR88wSinBkJ0kuL7C6CNgfBP2hd8gnY6VVHXeXllxGQ"
                ))
                .build();
    }
}