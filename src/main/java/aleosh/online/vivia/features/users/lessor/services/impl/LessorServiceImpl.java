package aleosh.online.vivia.features.users.lessor.services.impl;

import aleosh.online.vivia.features.auth.data.dtos.response.AuthResponseDto;
import aleosh.online.vivia.features.auth.data.entities.WebAuthnCredentialEntity;
import aleosh.online.vivia.features.auth.data.repositories.WebAuthnCredentialRepository;
import aleosh.online.vivia.features.auth.services.impl.GoogleTokenVerifierServiceImpl;
import aleosh.online.vivia.features.users.lessor.data.dtos.request.RegisterLessorBiometricChallengeDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.request.RegisterLessorBiometricVerifyDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.request.RegisterLessorGoogleDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.request.RegisterLessorPasswordDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.request.VerificationUploadRequestDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.response.VerificationRejectionResponseDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.response.VerificationStatusResponseDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.response.VerificationUploadResponseDto;
import aleosh.online.vivia.features.users.lessor.data.entities.LessorDocumentEntity;
import aleosh.online.vivia.features.users.lessor.data.repositories.LessorDocumentRepository;
import aleosh.online.vivia.features.users.lessor.data.repositories.VerificationRejectionRepository;
import aleosh.online.vivia.features.users.lessor.domain.objectvalues.DocumentType;
import aleosh.online.vivia.features.users.lessor.domain.objectvalues.VerificationStatus;
import aleosh.online.vivia.features.users.lessor.services.IVerificationPresignService;
import aleosh.online.vivia.core.config.jwt.JwtProvider;
import aleosh.online.vivia.core.config.security.CustomUserDetails;
import aleosh.online.vivia.features.auth.data.entities.CredentialEntity;
import aleosh.online.vivia.features.auth.domain.objectvalues.CredentialType;
import aleosh.online.vivia.features.auth.services.impl.RefreshTokenServiceImpl;
import aleosh.online.vivia.features.users.users.data.entities.UserEntity;
import aleosh.online.vivia.features.users.users.data.repositories.UserRepository;
import aleosh.online.vivia.features.users.users.domain.exceptions.UserAlreadyExistsException;
import aleosh.online.vivia.features.users.users.domain.repositories.IUserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.yubico.webauthn.FinishRegistrationOptions;
import com.yubico.webauthn.RegistrationResult;
import com.yubico.webauthn.StartRegistrationOptions;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.AuthenticatorAttachment;
import com.yubico.webauthn.data.AuthenticatorSelectionCriteria;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.ResidentKeyRequirement;
import com.yubico.webauthn.data.UserIdentity;
import com.yubico.webauthn.data.UserVerificationRequirement;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;

import aleosh.online.vivia.features.users.lessor.data.dtos.response.LessorResponseDto;
import aleosh.online.vivia.features.users.lessor.data.entities.LessorEntity;
import aleosh.online.vivia.features.users.lessor.data.repositories.LessorRepository;
import aleosh.online.vivia.features.users.lessor.data.dtos.request.UpdateLessorPhoneRequestDto;
import aleosh.online.vivia.features.users.lessor.domain.exceptions.LessorNotFoundException;
import aleosh.online.vivia.features.users.lessor.services.ILessorService;
import aleosh.online.vivia.features.users.lessor.services.mappers.LessorMapper;
import aleosh.online.vivia.features.users.users.domain.exceptions.BiometricException;
import aleosh.online.vivia.features.users.users.domain.exceptions.UntrustedAttestationException;
import aleosh.online.vivia.features.auth.domain.exceptions.InvalidChallengeException;
import aleosh.online.vivia.core.exceptions.DomainException;
import org.springframework.http.HttpStatus;
import com.yubico.webauthn.RelyingParty;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import aleosh.online.vivia.features.users.lessee.data.dtos.response.LesseeResponseDto;
import aleosh.online.vivia.features.users.lessee.data.repositories.LesseeRepository;

@Service
public class LessorServiceImpl implements ILessorService {

    private final LessorRepository lessorRepository;
    private final IUserRepository userDomainRepository; // Para lógica de negocio
    private final UserRepository userJpaRepository; // Para persistencia final
    private final LessorMapper lessorMapper;
    private final RelyingParty relyingParty;
    private final PasswordEncoder passwordEncoder;
    private final LesseeRepository lesseeRepository;
    private final JwtProvider jwtProvider;
    private final RefreshTokenServiceImpl refreshTokenServiceImpl;
    private final GoogleTokenVerifierServiceImpl googleTokenVerifierService;
    private final WebAuthnCredentialRepository webAuthnCredentialRepository;
    private final IVerificationPresignService verificationPresignService;
    private final LessorDocumentRepository lessorDocumentRepository;
    private final VerificationRejectionRepository verificationRejectionRepository;

    // Caché temporal para almacenar datos de registro biométrico
    private final Map<String, BiometricRegistrationData> registrationCache = new ConcurrentHashMap<>();

    public LessorServiceImpl(
            LessorRepository lessorRepository,
            IUserRepository userDomainRepository,
            UserRepository userJpaRepository,
            @org.springframework.beans.factory.annotation.Qualifier("lessorServiceMapper") LessorMapper lessorMapper,
            RelyingParty relyingParty,
            PasswordEncoder passwordEncoder,
            LesseeRepository lesseeRepository,
            JwtProvider jwtProvider,
            RefreshTokenServiceImpl refreshTokenServiceImpl,
            GoogleTokenVerifierServiceImpl googleTokenVerifierService,
            WebAuthnCredentialRepository webAuthnCredentialRepository,
            IVerificationPresignService verificationPresignService,
            LessorDocumentRepository lessorDocumentRepository,
            VerificationRejectionRepository verificationRejectionRepository
    ) {
        this.lessorRepository = lessorRepository;
        this.userDomainRepository = userDomainRepository;
        this.userJpaRepository = userJpaRepository;
        this.lessorMapper = lessorMapper;
        this.relyingParty = relyingParty;
        this.passwordEncoder = passwordEncoder;
        this.lesseeRepository = lesseeRepository;
        this.jwtProvider = jwtProvider;
        this.refreshTokenServiceImpl = refreshTokenServiceImpl;
        this.googleTokenVerifierService = googleTokenVerifierService;
        this.webAuthnCredentialRepository = webAuthnCredentialRepository;
        this.verificationPresignService = verificationPresignService;
        this.lessorDocumentRepository = lessorDocumentRepository;
        this.verificationRejectionRepository = verificationRejectionRepository;
    }

    @Override
    @Transactional
    public AuthResponseDto registerWithPassword(RegisterLessorPasswordDto request) {

        // 1. Validar si el email ya existe usando tu excepción de dominio
        if (userDomainRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email " + request.getEmail() + " is already taken.");
        }

        // 2. Crear la entidad UserEntity (Sin guardar aún)
        UserEntity userEntity = UserEntity.builder()
                .name(request.getName())
                .paternalSurname(request.getPaternalSurname())
                .maternalSurname(request.getMaternalSurname())
                .email(request.getEmail())
                .photoUrl("No photo")
                .build();

        // 3. Crear CredentialEntity y vincularla
        CredentialEntity credentialEntity = CredentialEntity.builder()
                .user(userEntity)
                .credentialType(CredentialType.PASSWORD)
                .secretData(passwordEncoder.encode(request.getPassword()))
                .build();
        userEntity.getCredentials().add(credentialEntity);

        // 4. Crear Perfil LessorEntity y vincularlo
        LessorEntity lessorEntity = LessorEntity.builder()
                .user(userEntity)
                .phoneNumber(request.getPhoneNumber())
                .build();
        userEntity.setLessor(lessorEntity);

        // 5. Guardar TODO de una sola vez. 
        // CascadeType.ALL en UserEntity se encargará de guardar el Lessor y las Credenciales.
        userJpaRepository.save(userEntity);

        // 6. Generar tokens (Iniciar sesión de inmediato)
        String role = "ROLE_LESSOR";
        var authorities = Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority(role));
        CustomUserDetails userDetails = new CustomUserDetails(userEntity.getId(), request.getEmail(), null, authorities);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

        String jwt = jwtProvider.generateToken(auth);
        String refreshToken = refreshTokenServiceImpl.createRefreshToken(request.getEmail(), role);

        return new AuthResponseDto(jwt, refreshToken);
    }

    @Override
    @Transactional
    public AuthResponseDto registerWithGoogleAccount(RegisterLessorGoogleDto request) {
        GoogleIdToken.Payload payload = googleTokenVerifierService.verifyIdToken(request.getIdToken());

        String googleId = payload.getSubject();
        String email = payload.getEmail();

        // Extraer campos específicos para mayor precisión
        String givenName = (String) payload.get("given_name");
        String familyName = (String) payload.get("family_name");
        String pictureUrl = (String) payload.get("picture");
        String phoneNumber = (String) payload.get("phone_number");

        if (userDomainRepository.existsByEmail(email)) {
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

        LessorEntity lessorEntity = LessorEntity.builder()
                .user(userEntity)
                .phoneNumber(phoneNumber != null ? phoneNumber : "0000000000")
                .build();
        userEntity.setLessor(lessorEntity);

        userJpaRepository.save(userEntity);

        String role = "ROLE_LESSOR";
        var authorities = Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority(role));
        CustomUserDetails userDetails = new CustomUserDetails(userEntity.getId(), email, null, authorities);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

        String jwt = jwtProvider.generateToken(auth);
        String refreshToken = refreshTokenServiceImpl.createRefreshToken(email, role);

        return new AuthResponseDto(jwt, refreshToken);
    }

    @Override
    public String startBiometricRegistration(RegisterLessorBiometricChallengeDto dto) {
        // Verificar que el email no exista
        if (userJpaRepository.existsByEmail(dto.getEmail())) {
            throw new UserAlreadyExistsException("Email " + dto.getEmail() + " ya está registrado");
        }

        // Generar userHandle único (será el ID del usuario)
        UUID userId = UUID.randomUUID();
        ByteArray userHandle = new ByteArray(userId.toString().getBytes(StandardCharsets.UTF_8));

        // Crear opciones de registro WebAuthn con resident key
        PublicKeyCredentialCreationOptions options = relyingParty.startRegistration(
            StartRegistrationOptions.builder()
                .user(UserIdentity.builder()
                    .name(dto.getEmail())
                    .displayName(dto.getName() + " " + dto.getPaternalSurname())
                    .id(userHandle)
                    .build())
                .authenticatorSelection(AuthenticatorSelectionCriteria.builder()
                    .authenticatorAttachment(AuthenticatorAttachment.PLATFORM)
                    .residentKey(ResidentKeyRequirement.REQUIRED)
                    .userVerification(UserVerificationRequirement.PREFERRED)
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
    public AuthResponseDto finishBiometricRegistration(RegisterLessorBiometricVerifyDto dto) {
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

            RegisterLessorBiometricChallengeDto userData = registrationData.getUserData();

            // Obtener el userHandle que se usó en el challenge
            String userHandle = registrationData.getOptions().getUser().getId().getBase64Url();

            // Crear usuario (el ID será generado automáticamente por Hibernate)
            UserEntity userEntity = UserEntity.builder()
                .name(userData.getName())
                .paternalSurname(userData.getPaternalSurname())
                .maternalSurname(userData.getMaternalSurname())
                .email(userData.getEmail())
                .photoUrl("No photo")
                .build();

            // Crear credencial tipo BIOMETRIC
            CredentialEntity credentialEntity = CredentialEntity.builder()
                .user(userEntity)
                .credentialType(CredentialType.BIOMETRIC)
                .providerCredentialId(result.getKeyId().getId().getBase64Url())
                .secretData(null)
                .build();
            userEntity.getCredentials().add(credentialEntity);

            // Crear credencial WebAuthn con userHandle
            WebAuthnCredentialEntity webAuthnCred = WebAuthnCredentialEntity.builder()
                .credentialId(result.getKeyId().getId().getBase64Url())
                .userHandle(userHandle)
                .user(userEntity)
                .publicKey(result.getPublicKeyCose().getBase64())
                .signCount(result.getSignatureCount())
                .aaguid(Optional.ofNullable(result.getAaguid()).map(ba -> ba.getBase64Url()).orElse(null))
                .build();

            // Crear Lessor con phoneNumber
            LessorEntity lessorEntity = LessorEntity.builder()
                .user(userEntity)
                .phoneNumber(userData.getPhoneNumber())
                .build();
            userEntity.setLessor(lessorEntity);

            // Guardar en BD
            userJpaRepository.save(userEntity);
            webAuthnCredentialRepository.save(webAuthnCred);

            // Generar tokens
            String role = "ROLE_LESSOR";
            var authorities = Collections.singletonList(
                new org.springframework.security.core.authority.SimpleGrantedAuthority(role)
            );
            CustomUserDetails userDetails = new CustomUserDetails(userEntity.getId(), userData.getEmail(), null, authorities);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, authorities
            );

            String jwt = jwtProvider.generateToken(auth);
            String refreshToken = refreshTokenServiceImpl.createRefreshToken(
                userData.getEmail(), role
            );

            // Limpiar caché
            registrationCache.remove(challengeId);

            return new AuthResponseDto(jwt, refreshToken);

        } catch (DomainException e) {
            throw e;
        } catch (Exception e) {
            throw new BiometricException("Error en verificación de registro biométrico: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public void updatePhoneNumber(UUID lessorId, UpdateLessorPhoneRequestDto dto) {
        if (!lessorRepository.existsById(lessorId)) {
            throw new LessorNotFoundException("Lessor " + lessorId + " no encontrado.");
        }
        lessorRepository.updatePhoneNumber(lessorId, dto.getPhoneNumber());
    }

    @Override
    @Transactional
    public VerificationUploadResponseDto requestVerificationUpload(UUID lessorId, VerificationUploadRequestDto dto) {
        if (!lessorRepository.existsById(lessorId)) {
            throw new LessorNotFoundException("Lessor " + lessorId + " no encontrado.");
        }
        lessorRepository.updateVerificationStatus(lessorId, VerificationStatus.PENDING_REVIEW);
        return verificationPresignService.generateUploadUrls(lessorId, dto);
    }

    @Override
    @Transactional
    public void saveVerificationDocument(UUID lessorId, DocumentType documentType, String publicUrl) {
        LessorEntity lessor = lessorRepository.findById(lessorId)
                .orElseThrow(() -> new LessorNotFoundException("Lessor " + lessorId + " no encontrado."));

        LessorDocumentEntity document = LessorDocumentEntity.builder()
                .lessor(lessor)
                .documentType(documentType)
                .uri(publicUrl)
                .build();

        lessorDocumentRepository.save(document);
    }

    @Override
    public boolean allVerificationDocumentsUploaded(UUID lessorId) {
        long distinctTypes = lessorDocumentRepository.countDistinctDocumentTypesByLessorId(lessorId);
        return distinctTypes >= DocumentType.values().length;
    }

    @Override
    public VerificationStatusResponseDto getVerificationStatus(UUID lessorId) {
        LessorEntity lessor = lessorRepository.findById(lessorId)
                .orElseThrow(() -> new LessorNotFoundException("Lessor " + lessorId + " no encontrado."));

        VerificationRejectionResponseDto rejection = verificationRejectionRepository
                .findByLessor_Id(lessorId)
                .map(r -> new VerificationRejectionResponseDto(r.getComment(), r.getReasons(), r.getCreatedAt()))
                .orElse(null);

        return new VerificationStatusResponseDto(lessor.getVerificationStatus().name(), rejection);
    }

    @Override
    @Transactional
    public void resetVerificationStatus(UUID lessorId) {
        if (!lessorRepository.existsById(lessorId)) {
            throw new LessorNotFoundException("Lessor " + lessorId + " no encontrado.");
        }
        lessorDocumentRepository.deleteByLessorId(lessorId);
        verificationRejectionRepository.findByLessor_Id(lessorId).ifPresent(existing -> {
            verificationRejectionRepository.delete(existing);
            verificationRejectionRepository.flush();
        });
        lessorRepository.updateVerificationStatus(lessorId, VerificationStatus.UNVERIFIED);
    }

    // Clase interna para almacenar datos temporales de registro
    @Data
    @AllArgsConstructor
    private static class BiometricRegistrationData {
        private PublicKeyCredentialCreationOptions options;
        private RegisterLessorBiometricChallengeDto userData;
        private UUID userId;
    }
}