package aleosh.online.vivia.features.auth.services.impl;

import aleosh.online.vivia.core.config.security.CustomUserDetails;
import aleosh.online.vivia.core.config.jwt.JwtProvider;
import aleosh.online.vivia.features.auth.data.dtos.request.BiometricLoginChallengeDto;
import aleosh.online.vivia.features.auth.data.dtos.request.GoogleLoginRequestDto;
import aleosh.online.vivia.features.auth.data.dtos.request.VerifyLoginDto;
import aleosh.online.vivia.features.auth.data.dtos.response.AuthResponseDto;
import aleosh.online.vivia.features.auth.data.entities.CredentialEntity;
import aleosh.online.vivia.features.auth.data.entities.WebAuthnCredentialEntity;
import aleosh.online.vivia.features.auth.data.repositories.CredentialRepository;
import aleosh.online.vivia.features.auth.data.repositories.RedisLoginCacheRepository;
import aleosh.online.vivia.features.auth.data.repositories.WebAuthnCredentialRepository;
import aleosh.online.vivia.features.auth.domain.objectvalues.CredentialType;
import aleosh.online.vivia.features.auth.services.IAuthService;
import aleosh.online.vivia.features.users.users.data.entities.UserEntity;
import aleosh.online.vivia.features.users.users.data.repositories.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.AssertionResult;
import com.yubico.webauthn.FinishAssertionOptions;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.StartAssertionOptions;
import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import aleosh.online.vivia.features.auth.data.dtos.request.RefreshTokenRequestDto;
import aleosh.online.vivia.features.auth.data.models.RefreshTokenData;
import aleosh.online.vivia.features.auth.data.dtos.request.LoginRequestDto;
import aleosh.online.vivia.features.auth.domain.exceptions.AuthException;
import aleosh.online.vivia.features.auth.domain.exceptions.InvalidChallengeException;
import aleosh.online.vivia.core.exceptions.DomainException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class AuthServiceImpl implements IAuthService {

    private final JwtProvider jwtProvider;
    private final RelyingParty relyingParty;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenServiceImpl refreshTokenServiceImpl;
    private final GoogleTokenVerifierServiceImpl googleTokenVerifierService;
    private final CredentialRepository credentialRepository;
    private final WebAuthnCredentialRepository webAuthnCredentialRepository;
    private final UserRepository userRepository;
    private final RedisLoginCacheRepository redisLoginCacheRepository;

    public AuthServiceImpl(
            JwtProvider jwtProvider,
            RelyingParty relyingParty,
            UserDetailsServiceImpl userDetailsService,
            AuthenticationManager authenticationManager,
            RefreshTokenServiceImpl refreshTokenServiceImpl,
            GoogleTokenVerifierServiceImpl googleTokenVerifierService,
            CredentialRepository credentialRepository,
            WebAuthnCredentialRepository webAuthnCredentialRepository,
            UserRepository userRepository,
            RedisLoginCacheRepository redisLoginCacheRepository
    ) {
        this.jwtProvider = jwtProvider;
        this.relyingParty = relyingParty;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.refreshTokenServiceImpl = refreshTokenServiceImpl;
        this.googleTokenVerifierService = googleTokenVerifierService;
        this.credentialRepository = credentialRepository;
        this.webAuthnCredentialRepository = webAuthnCredentialRepository;
        this.userRepository = userRepository;
        this.redisLoginCacheRepository = redisLoginCacheRepository;
    }

    @Override
    public String startLogin(BiometricLoginChallengeDto dto) {
        // Verificar que el usuario existe
        UserEntity user = userRepository.findByEmail(dto.getEmail())
            .orElseThrow(() -> new AuthException(
                "No existe un usuario con el email: " + dto.getEmail(),
                HttpStatus.NOT_FOUND
            ));

        // Verificar que el usuario tiene credenciales biométricas
        boolean hasBiometricCredentials = user.getCredentials().stream()
            .anyMatch(cred -> cred.getCredentialType() == CredentialType.BIOMETRIC);

        if (!hasBiometricCredentials) {
            throw new AuthException(
                "El usuario no tiene credenciales biométricas registradas",
                HttpStatus.BAD_REQUEST
            );
        }

        // Iniciar assertion con el username - esto hará que allowCredentials se incluya
        AssertionRequest request = relyingParty.startAssertion(
            StartAssertionOptions.builder()
                .username(dto.getEmail())
                .build()
        );

        String challengeId = request.getPublicKeyCredentialRequestOptions().getChallenge().getBase64Url();
        redisLoginCacheRepository.save(challengeId, request);

        try {
            return request.toCredentialsGetJson();
        } catch (Exception e) {
            throw new AuthException("Error al generar opciones de login", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @Override
    public AuthResponseDto finishLogin(VerifyLoginDto verifyDto) {
        try {
            PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> pkc =
                    PublicKeyCredential.parseAssertionResponseJson(verifyDto.getCredentialResponseJson());

            // Extraemos el desafío devuelto por el dispositivo para encontrar la petición original en Redis
            String challengeId = pkc.getResponse().getClientData().getChallenge().getBase64Url();
            AssertionRequest pendingAssertion = redisLoginCacheRepository.find(challengeId)
                .orElseThrow(() -> new InvalidChallengeException("Desafío de login expirado o inválido."));

            // Validar firma criptográfica
            AssertionResult result = relyingParty.finishAssertion(FinishAssertionOptions.builder()
                    .request(pendingAssertion)
                    .response(pkc)
                    .build());

            if (result.isSuccess()) {
                // 1. Obtener credential_id de la respuesta
                String credentialId = result.getCredentialId().getBase64Url();

                // 2. Buscar la credencial en la BD (NO por email)
                WebAuthnCredentialEntity credential = webAuthnCredentialRepository
                    .findByCredentialId(credentialId)
                    .orElseThrow(() -> new AuthException(
                        "Credencial no encontrada",
                        HttpStatus.NOT_FOUND
                    ));

                // 3. Actualizar el contador de firmas (prevención de ataques de clonación)
                credential.setSignCount(result.getSignatureCount());
                webAuthnCredentialRepository.save(credential);

                // 4. Obtener el usuario de la credencial
                UserEntity user = credential.getUser();
                String email = user.getEmail();

                // 5. Determinar el rol del usuario
                String role;
                if (user.getLessor() != null) {
                    role = "ROLE_LESSOR";
                } else if (user.getLessee() != null) {
                    role = "ROLE_LESSEE";
                } else {
                    throw new AuthException("El usuario no tiene un rol asignado", HttpStatus.CONFLICT);
                }

                // 6. Generar autenticación y tokens
                var authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
                CustomUserDetails userDetails = new CustomUserDetails(
                    user.getId(),
                    email,
                    null,
                    authorities
                );
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, authorities
                );
                SecurityContextHolder.getContext().setAuthentication(auth);

                String jwt = jwtProvider.generateToken(auth);
                String refreshToken = refreshTokenServiceImpl.createRefreshToken(email, role);

                redisLoginCacheRepository.remove(challengeId);

                return new AuthResponseDto(jwt, refreshToken);
            } else {
                throw new AuthException("La validación de la huella falló.");
            }

        } catch (DomainException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthException("Error en el proceso de autenticación WebAuthn: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public AuthResponseDto traditionalLogin(LoginRequestDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getIdentifier(), loginDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateToken(authentication);

        String role = authentication.getAuthorities().iterator().next().getAuthority();
        String refreshToken = refreshTokenServiceImpl.createRefreshToken(loginDto.getIdentifier(), role);

        return new AuthResponseDto(jwt, refreshToken);
    }

    @Override
    @Transactional
    public AuthResponseDto googleLogin(GoogleLoginRequestDto googleLoginDto) {
        GoogleIdToken.Payload payload = googleTokenVerifierService.verifyIdToken(googleLoginDto.getIdToken());

        String googleId = payload.getSubject();
        String email = payload.getEmail();

        // Cargar credencial con user, lessor y lessee en una sola query optimizada
        CredentialEntity credentialEntity = credentialRepository
                .findByProviderCredentialIdAndCredentialTypeWithUser(googleId, CredentialType.GOOGLE)
                .orElseThrow(() -> new AuthException(
                    "No existe una cuenta vinculada a este Google ID. Por favor, regístrate primero.",
                    HttpStatus.NOT_FOUND
                ));

        // Obtener el usuario y determinar el rol real desde la base de datos
        UserEntity userEntity = credentialEntity.getUser();

        String roleFromDB;
        if (userEntity.getLessor() != null) {
            roleFromDB = "ROLE_LESSOR";
        } else if (userEntity.getLessee() != null) {
            roleFromDB = "ROLE_LESSEE";
        } else {
            throw new AuthException("El usuario no tiene un rol asignado", HttpStatus.CONFLICT);
        }

        // Validar que el rol enviado por el cliente coincide con el de la BD
        if (!roleFromDB.equals(googleLoginDto.getRole())) {
            throw new AuthException(
                "El rol proporcionado (" + googleLoginDto.getRole() +
                ") no coincide con el rol del usuario registrado (" + roleFromDB + ")",
                HttpStatus.FORBIDDEN
            );
        }

        var authorities = Collections.singletonList(new SimpleGrantedAuthority(roleFromDB));
        CustomUserDetails userDetails = new CustomUserDetails(
                userEntity.getId(),
                email,
                null,
                authorities
        );
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, authorities
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = jwtProvider.generateToken(auth);
        String refreshToken = refreshTokenServiceImpl.createRefreshToken(email, roleFromDB);

        return new AuthResponseDto(jwt, refreshToken);
    }

    @Override
    public AuthResponseDto refreshToken(RefreshTokenRequestDto request) {
        String requestRefreshToken = request.getRefreshToken();

        RefreshTokenData refreshTokenData = refreshTokenServiceImpl.findByToken(requestRefreshToken);
        refreshTokenServiceImpl.verifyExpiration(refreshTokenData, requestRefreshToken);

        String userIdentifier = refreshTokenData.getUserIdentifier();
        String role = refreshTokenData.getRole();

        UserDetails userDetails = userDetailsService.loadUserByIdentifierAndRole(userIdentifier, role);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        String jwt = jwtProvider.generateToken(auth);

        // Rotation
        String newRefreshToken = refreshTokenServiceImpl.createRefreshToken(userIdentifier, role);

        return new AuthResponseDto(jwt, newRefreshToken);
    }
}