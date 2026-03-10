package aleosh.online.vivia.features.users.lessee.services.impl;

import aleosh.online.vivia.features.users.lessee.data.dtos.request.CreateLesseeDto;
import aleosh.online.vivia.features.users.lessee.data.dtos.request.VerifyLesseeRegistrationDto;
import aleosh.online.vivia.features.users.lessee.data.dtos.response.LesseeResponseDto;
import aleosh.online.vivia.features.users.lessee.data.entities.LesseeEntity;
import aleosh.online.vivia.features.users.lessee.data.repositories.LesseeRepository;
import aleosh.online.vivia.features.users.lessee.services.ILesseeService;
import aleosh.online.vivia.features.users.lessee.services.mappers.LesseeMapper;
import aleosh.online.vivia.features.users.lessor.data.entities.PasskeyCredentialEntity;
import com.yubico.webauthn.*;
import com.yubico.webauthn.data.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class LesseeServiceImpl implements ILesseeService {

    private final LesseeRepository lesseeRepository;
    private final LesseeMapper lesseeMapper;
    private final RelyingParty relyingParty;

    private final Map<String, RegistrationRequestState> registrationCache = new ConcurrentHashMap<>();

    public LesseeServiceImpl(
            LesseeRepository lesseeRepository,
            @Qualifier("lesseeServiceMapper") LesseeMapper lesseeMapper,
            RelyingParty relyingParty
    ) {
        this.lesseeRepository = lesseeRepository;
        this.lesseeMapper = lesseeMapper;
        this.relyingParty = relyingParty;
    }

    @Override
    public String startRegistration(CreateLesseeDto dto) {
        if (lesseeRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("El arrendatario con este nombre de usuario ya existe.");
        }
        if (lesseeRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("El arrendatario con este correo ya existe.");
        }

        // Generamos un ID interno único para la identidad WebAuthn
        byte[] userHandle = new byte[32];
        new SecureRandom().nextBytes(userHandle);

        UserIdentity userIdentity = UserIdentity.builder()
                .name(dto.getEmail())
                .displayName(dto.getUsername())
                .id(new ByteArray(userHandle))
                .build();

        StartRegistrationOptions startOpts = StartRegistrationOptions.builder()
                .user(userIdentity)
                .build();

        // El motor criptográfico genera el desafío y los parámetros
        PublicKeyCredentialCreationOptions options = relyingParty.startRegistration(startOpts);

        // Almacenamos el estado pendiente vinculado al nombre de usuario
        registrationCache.put(dto.getEmail(), new RegistrationRequestState(dto, options));

        try {
            // Devolvemos las opciones serializadas para que la app móvil inicie el escáner de huellas
            return options.toCredentialsCreateJson();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar las opciones de registro WebAuthn", e);
        }
    }

    @Override
    public LesseeResponseDto finishRegistration(VerifyLesseeRegistrationDto verifyDto) {
        RegistrationRequestState pendingRequest = registrationCache.get(verifyDto.getEmail());
        if (pendingRequest == null) {
            throw new RuntimeException("No hay un proceso de registro activo para este usuario o el desafío ha expirado.");
        }

        try {
            // 1. Parsear el JSON recibido del celular
            PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> pkc =
                    PublicKeyCredential.parseRegistrationResponseJson(verifyDto.getCredentialResponseJson());

            // 2. Ejecutar la validación criptográfica contra el desafío original guardado en caché
            RegistrationResult result = relyingParty.finishRegistration(FinishRegistrationOptions.builder()
                    .request(pendingRequest.options)
                    .response(pkc)
                    .build());

            // 3. Si no se lanzó excepción, la biometría es válida. Procedemos a crear las entidades.
            CreateLesseeDto originalDto = pendingRequest.originalDto;
            LesseeEntity lesseeEntity = new LesseeEntity();
            lesseeEntity.setUserHandle(pendingRequest.options.getUser().getId().getBytes());
            lesseeEntity.setUsername(originalDto.getUsername());
            lesseeEntity.setEmail(originalDto.getEmail());

            PasskeyCredentialEntity credentialEntity = new PasskeyCredentialEntity();
            credentialEntity.setCredentialId(result.getKeyId().getId().getBytes());
            credentialEntity.setPublicKey(result.getPublicKeyCose().getBytes());
            credentialEntity.setSignCount(result.getSignatureCount());

            lesseeEntity.addCredential(credentialEntity);

            LesseeEntity savedLesseeEntity = lesseeRepository.save(lesseeEntity);

            // 4. Limpiar la caché temporal
            registrationCache.remove(verifyDto.getEmail());

            return lesseeMapper.toLesseeResponseDto(savedLesseeEntity);

        } catch (Exception e) {
            throw new RuntimeException("Fallo en la validación criptográfica de la credencial", e);
        }
    }

    // Clase interna para manejar el estado temporal
    private static class RegistrationRequestState {
        final CreateLesseeDto originalDto;
        final PublicKeyCredentialCreationOptions options;

        RegistrationRequestState(CreateLesseeDto originalDto, PublicKeyCredentialCreationOptions options) {
            this.originalDto = originalDto;
            this.options = options;
        }
    }

    @Override
    public LesseeResponseDto getLesseeByUsername(String username) {
        return lesseeRepository.findByUsername(username)
                .map(lesseeMapper::toLesseeResponseDto)
                .orElseThrow(() -> new RuntimeException("No existe el arrendatario"));
    }

    @Override
    public LesseeResponseDto getLesseeByEmail(String email) {
        return lesseeRepository.findByEmail(email)
                .map(lesseeMapper::toLesseeResponseDto)
                .orElseThrow(() -> new RuntimeException("No existe el arrendatario"));
    }

    @Override
    public List<LesseeResponseDto> getAllLessees() {
        return lesseeRepository.findAll().stream()
                .map(lesseeMapper::toLesseeResponseDto)
                .collect(Collectors.toList());
    }
}