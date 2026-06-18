package aleosh.online.vivia.features.users.lessee.services.impl;

import aleosh.online.vivia.core.config.jwt.JwtProvider;
import aleosh.online.vivia.features.auth.data.dtos.response.AuthResponseDto;
import aleosh.online.vivia.features.auth.data.entities.CredentialEntity;
import aleosh.online.vivia.features.auth.data.entities.WebAuthnCredentialEntity;
import aleosh.online.vivia.features.auth.data.repositories.WebAuthnCredentialRepository;
import aleosh.online.vivia.features.auth.domain.objectvalues.CredentialType;
import aleosh.online.vivia.features.auth.services.impl.GoogleTokenVerifierServiceImpl;
import aleosh.online.vivia.features.auth.services.impl.RefreshTokenServiceImpl;
import aleosh.online.vivia.features.users.lessee.data.dtos.request.RegisterLesseeBiometricChallengeDto;
import aleosh.online.vivia.features.users.lessee.data.dtos.request.RegisterLesseeBiometricVerifyDto;
import aleosh.online.vivia.features.users.lessee.data.dtos.request.RegisterLesseeGoogleDto;
import aleosh.online.vivia.features.users.lessee.data.dtos.request.RegisterLesseePasswordDto;
import aleosh.online.vivia.features.users.lessee.data.entities.LesseeEntity;
import aleosh.online.vivia.features.users.lessee.data.repositories.LesseeRepository;
import aleosh.online.vivia.features.users.lessee.services.ILesseeService;
import aleosh.online.vivia.features.users.users.data.entities.UserEntity;
import aleosh.online.vivia.features.users.users.data.repositories.UserRepository;
import aleosh.online.vivia.features.users.users.domain.exceptions.UserAlreadyExistsException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import aleosh.online.vivia.features.users.users.domain.exceptions.BiometricException;
import aleosh.online.vivia.features.users.users.domain.exceptions.UntrustedAttestationException;
import aleosh.online.vivia.features.auth.domain.exceptions.InvalidChallengeException;
import aleosh.online.vivia.core.exceptions.DomainException;
import org.springframework.http.HttpStatus;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.RegistrationResult;
import com.yubico.webauthn.FinishRegistrationOptions;
import com.yubico.webauthn.StartRegistrationOptions;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.UserIdentity;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LesseeServiceImpl implements ILesseeService {

    private final LesseeRepository lesseeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenServiceImpl refreshTokenServiceImpl;
    private final GoogleTokenVerifierServiceImpl googleTokenVerifierService;
    private final RelyingParty relyingParty;
    private final WebAuthnCredentialRepository webAuthnCredentialRepository;

    // Caché temporal para almacenar datos de registro biométrico
    private final Map<String, BiometricRegistrationData> registrationCache = new ConcurrentHashMap<>();

    public LesseeServiceImpl(
            LesseeRepository lesseeRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtProvider jwtProvider,
            RefreshTokenServiceImpl refreshTokenServiceImpl,
            GoogleTokenVerifierServiceImpl googleTokenVerifierService,
            RelyingParty relyingParty,
            WebAuthnCredentialRepository webAuthnCredentialRepository
    ) {
        this.lesseeRepository = lesseeRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.refreshTokenServiceImpl = refreshTokenServiceImpl;
        this.googleTokenVerifierService = googleTokenVerifierService;
        this.relyingParty = relyingParty;
        this.webAuthnCredentialRepository = webAuthnCredentialRepository;
    }


    @Override
    @Transactional
    public AuthResponseDto registerWithPassword(RegisterLesseePasswordDto request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email " + request.getEmail() + " is already taken.");
        }

        UserEntity userEntity = UserEntity.builder()
                .name(request.getName())
                .paternalSurname(request.getPaternalSurname())
                .maternalSurname(request.getMaternalSurname())
                .email(request.getEmail())
                .build();

        CredentialEntity credentialEntity = CredentialEntity.builder()
                .user(userEntity)
                .credentialType(CredentialType.PASSWORD)
                .secretData(passwordEncoder.encode(request.getPassword()))
                .build();
        userEntity.getCredentials().add(credentialEntity);

        LesseeEntity lesseeEntity = LesseeEntity.builder()
                .user(userEntity)
                .build();
        userEntity.setLessee(lesseeEntity);

        userRepository.save(userEntity);

        String role = "ROLE_LESSEE";
        var authorities = Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority(role));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(request.getEmail(), null, authorities);

        String jwt = jwtProvider.generateToken(auth);
        String refreshToken = refreshTokenServiceImpl.createRefreshToken(request.getEmail(), role).getToken();

        return new AuthResponseDto(jwt, refreshToken);
    }

    @Override
    @Transactional
    public AuthResponseDto registerWithGoogleAccount(RegisterLesseeGoogleDto request) {
        GoogleIdToken.Payload payload = googleTokenVerifierService.verifyIdToken(request.getIdToken());

        String googleId = payload.getSubject();
        String email = payload.getEmail();

        // Extraer campos específicos para mayor precisión
        String givenName = (String) payload.get("given_name");
        String familyName = (String) payload.get("family_name");
        String pictureUrl = (String) payload.get("picture");

        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Email " + email + " is already taken.");
        }

        // Lógica de división de apellidos (paterno y materno)
        String paternal = "";
        String maternal = "";
        if (familyName != null && !familyName.isBlank()) {
            String[] parts = familyName.trim().split("\\s+", 2);
            paternal = parts[0];
            if (parts.length > 1) {
                maternal = parts[1];
            }
        }

        UserEntity userEntity = UserEntity.builder()
                .name(givenName != null ? givenName : (String) payload.get("name"))
                .paternalSurname(paternal)
                .maternalSurname(maternal)
                .email(email)
                .photoUrl(pictureUrl != null ? pictureUrl : "No photo")
                .build();

        CredentialEntity credentialEntity = CredentialEntity.builder()
                .user(userEntity)
                .credentialType(CredentialType.GOOGLE)
                .providerCredentialId(googleId)
                .build();
        userEntity.getCredentials().add(credentialEntity);

        LesseeEntity lesseeEntity = LesseeEntity.builder()
                .user(userEntity)
                .build();
        userEntity.setLessee(lesseeEntity);

        String role = "ROLE_LESSEE";
        var authorities = Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority(role));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, null, authorities);

        String jwt = jwtProvider.generateToken(auth);
        String refreshToken = refreshTokenServiceImpl.createRefreshToken(email, role).getToken();

        return new AuthResponseDto(jwt, refreshToken);
    }

    @Override
    public String startBiometricRegistration(RegisterLesseeBiometricChallengeDto dto) {
        // Verificar que el email no exista
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new UserAlreadyExistsException("Email " + dto.getEmail() + " ya está registrado");
        }

        // Generar userHandle único (será el ID del usuario)
        UUID userId = UUID.randomUUID();
        ByteArray userHandle = new ByteArray(userId.toString().getBytes(StandardCharsets.UTF_8));

        // Crear opciones de registro WebAuthn
        PublicKeyCredentialCreationOptions options = relyingParty.startRegistration(
            StartRegistrationOptions.builder()
                .user(UserIdentity.builder()
                    .name(dto.getEmail())
                    .displayName(dto.getName() + " " + dto.getPaternalSurname())
                    .id(userHandle)
                    .build())
                .build()
        );

        // Guardar en caché temporal usando el challenge como clave
        String challengeId = options.getChallenge().getBase64Url();
        registrationCache.put(challengeId, new BiometricRegistrationData(options, dto, userId));

        try {
            return options.toCredentialsCreateJson();
        } catch (Exception e) {
            registrationCache.remove(challengeId);
            throw new BiometricException("Error generando challenge de registro biométrico", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public AuthResponseDto finishBiometricRegistration(RegisterLesseeBiometricVerifyDto dto) {
        try {
            // Parse de la respuesta del dispositivo
            PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> pkc =
                PublicKeyCredential.parseRegistrationResponseJson(dto.getCredentialResponseJson());

            // Obtener el challenge de la respuesta
            String challengeId = pkc.getResponse().getClientData().getChallenge().getBase64Url();

            // Buscar datos en caché
            BiometricRegistrationData registrationData = registrationCache.get(challengeId);
            if (registrationData == null) {
                throw new InvalidChallengeException("Challenge de registro expirado o inválido");
            }

            // Verificar la firma criptográfica
            RegistrationResult result = relyingParty.finishRegistration(
                FinishRegistrationOptions.builder()
                    .request(registrationData.getOptions())
                    .response(pkc)
                    .build()
            );

            // Nota: No verificamos isAttestationTrusted() porque los dispositivos móviles
            // comúnmente usan "none attestation" por razones de privacidad.
            // La seguridad se basa en la firma criptográfica verificada arriba.

            RegisterLesseeBiometricChallengeDto userData = registrationData.getUserData();

            // Crear usuario
            UserEntity userEntity = UserEntity.builder()
                .id(registrationData.getUserId())
                .name(userData.getName())
                .paternalSurname(userData.getPaternalSurname())
                .maternalSurname(userData.getMaternalSurname())
                .email(userData.getEmail())
                .build();

            // Crear credencial tipo BIOMETRIC
            CredentialEntity credentialEntity = CredentialEntity.builder()
                .user(userEntity)
                .credentialType(CredentialType.BIOMETRIC)
                .providerCredentialId(result.getKeyId().getId().getBase64Url())
                .secretData(null)
                .build();
            userEntity.getCredentials().add(credentialEntity);

            // Crear credencial WebAuthn
            WebAuthnCredentialEntity webAuthnCred = WebAuthnCredentialEntity.builder()
                .credentialId(result.getKeyId().getId().getBase64Url())
                .user(userEntity)
                .publicKey(result.getPublicKeyCose().getBase64())
                .signCount(result.getSignatureCount())
                .aaguid(Optional.ofNullable(result.getAaguid()).map(ba -> ba.getBase64Url()).orElse(null))
                .build();

            // Crear Lessee
            LesseeEntity lesseeEntity = LesseeEntity.builder()
                .user(userEntity)
                .build();
            userEntity.setLessee(lesseeEntity);

            // Guardar en BD
            userRepository.save(userEntity);
            webAuthnCredentialRepository.save(webAuthnCred);

            // Generar tokens
            String role = "ROLE_LESSEE";
            var authorities = Collections.singletonList(
                new org.springframework.security.core.authority.SimpleGrantedAuthority(role)
            );
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userData.getEmail(), null, authorities
            );

            String jwt = jwtProvider.generateToken(auth);
            String refreshToken = refreshTokenServiceImpl.createRefreshToken(
                userData.getEmail(), role
            ).getToken();

            // Limpiar caché
            registrationCache.remove(challengeId);

            return new AuthResponseDto(jwt, refreshToken);

        } catch (DomainException e) {
            throw e;
        } catch (Exception e) {
            throw new BiometricException("Error en verificación de registro biométrico: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Clase interna para almacenar datos temporales de registro
    @Data
    @AllArgsConstructor
    private static class BiometricRegistrationData {
        private PublicKeyCredentialCreationOptions options;
        private RegisterLesseeBiometricChallengeDto userData;
        private UUID userId;
    }
}