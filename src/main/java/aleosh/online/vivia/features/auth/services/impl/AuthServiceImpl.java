package aleosh.online.vivia.features.auth.services.impl;

import aleosh.online.vivia.core.config.jwt.JwtProvider;
import aleosh.online.vivia.features.auth.data.dtos.request.VerifyLoginDto;
import aleosh.online.vivia.features.auth.data.dtos.response.AuthResponseDto;
import aleosh.online.vivia.features.auth.services.IAuthService;
import aleosh.online.vivia.features.users.lessor.data.repositories.PasskeyCredentialRepository;
import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.AssertionResult;
import com.yubico.webauthn.FinishAssertionOptions;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.StartAssertionOptions;
import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthServiceImpl implements IAuthService {

    private final JwtProvider jwtProvider;
    private final RelyingParty relyingParty;
    private final UserDetailsServiceImpl userDetailsService;
    private final PasskeyCredentialRepository passkeyRepository;

    // Caché para los desafíos de login. La clave es el propio desafío en Base64Url
    private final Map<String, AssertionRequest> loginCache = new ConcurrentHashMap<>();

    public AuthServiceImpl(
            JwtProvider jwtProvider,
            RelyingParty relyingParty,
            UserDetailsServiceImpl userDetailsService,
            PasskeyCredentialRepository passkeyRepository
    ) {
        this.jwtProvider = jwtProvider;
        this.relyingParty = relyingParty;
        this.userDetailsService = userDetailsService;
        this.passkeyRepository = passkeyRepository;
    }

    @Override
    public String startLogin() {
        // Al no enviar un usuario, forzamos el uso de "Discoverable Credentials" (solo huella)
        AssertionRequest request = relyingParty.startAssertion(StartAssertionOptions.builder().build());

        String challengeId = request.getPublicKeyCredentialRequestOptions().getChallenge().getBase64Url();
        loginCache.put(challengeId, request);

        try {
            return request.toCredentialsGetJson();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar opciones de login", e);
        }
    }

    @Transactional
    @Override
    public AuthResponseDto finishLogin(VerifyLoginDto verifyDto) {
        try {
            PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> pkc =
                    PublicKeyCredential.parseAssertionResponseJson(verifyDto.getCredentialResponseJson());

            // Extraemos el desafío devuelto por el dispositivo para encontrar la petición original en la caché
            String challengeId = pkc.getResponse().getClientData().getChallenge().getBase64Url();
            AssertionRequest pendingAssertion = loginCache.get(challengeId);

            if (pendingAssertion == null) {
                throw new RuntimeException("Desafío de login expirado o inválido.");
            }

            // Validar firma criptográfica
            AssertionResult result = relyingParty.finishAssertion(FinishAssertionOptions.builder()
                    .request(pendingAssertion)
                    .response(pkc)
                    .build());

            if (result.isSuccess()) {
                // Actualizamos el contador de firmas (prevención de ataques de clonación)
                passkeyRepository.findById(result.getCredential().getCredentialId().getBytes()).ifPresent(cred -> {
                    cred.setSignCount(result.getSignatureCount());
                    passkeyRepository.save(cred);
                });

                loginCache.remove(challengeId);

                // Yubico ya averiguó el nombre de usuario (companyName o email) usando nuestro Adapter
                String identifier = result.getUsername();

                // Carga de roles y generación de JWT de forma transparente usando tu infraestructura de la Fase 2
                UserDetails userDetails = userDetailsService.loadUserByUsername(identifier);

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);

                String jwt = jwtProvider.generateToken(auth);
                return new AuthResponseDto(jwt);
            } else {
                throw new RuntimeException("La validación de la huella falló.");
            }

        } catch (Exception e) {
            throw new RuntimeException("Error en el proceso de autenticación WebAuthn", e);
        }
    }
}