package aleosh.online.vivia.features.users.lessor.services.impl;

import aleosh.online.vivia.features.auth.data.dtos.response.AuthResponseDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.request.RegisterLessorPasswordDto;
import aleosh.online.vivia.core.config.jwt.JwtProvider;
import aleosh.online.vivia.features.auth.data.entities.CredentialEntity;
import aleosh.online.vivia.features.auth.domain.objectvalues.CredentialType;
import aleosh.online.vivia.features.auth.services.impl.RefreshTokenService;
import aleosh.online.vivia.features.users.users.data.entities.UserEntity;
import aleosh.online.vivia.features.users.users.data.repositories.UserRepository;
import aleosh.online.vivia.features.users.users.domain.exceptions.UserAlreadyExistsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import aleosh.online.vivia.features.users.lessor.data.dtos.response.LessorResponseDto;
import aleosh.online.vivia.features.users.lessor.data.entities.LessorEntity;
import aleosh.online.vivia.features.users.lessor.data.repositories.LessorRepository;
import aleosh.online.vivia.features.users.lessor.services.ILessorService;
import aleosh.online.vivia.features.users.lessor.services.mappers.LessorMapper;
//import aleosh.online.vivia.features.users.lessor.data.dtos.request.CreateLessorDto;
//import aleosh.online.vivia.features.users.lessor.data.dtos.request.VerifyLessorRegistrationDto;
import com.yubico.webauthn.RelyingParty;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.yubico.webauthn.FinishRegistrationOptions;
import com.yubico.webauthn.RegistrationResult;
import com.yubico.webauthn.StartRegistrationOptions;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.UserIdentity;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import aleosh.online.vivia.features.users.lessee.data.dtos.response.LesseeResponseDto;
import aleosh.online.vivia.features.users.lessee.data.repositories.LesseeRepository;

@Service
public class LessorServiceImpl implements ILessorService {

    private final LessorRepository lessorRepository;
    private final UserRepository userRepository;
    private final LessorMapper lessorMapper;
    private final RelyingParty relyingParty;
    private final PasswordEncoder passwordEncoder;
    private final LesseeRepository lesseeRepository;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    //private final Map<String, RegistrationRequestState> registrationCache = new ConcurrentHashMap<>();

    public LessorServiceImpl(
            LessorRepository lessorRepository,
            UserRepository userRepository,
            @org.springframework.beans.factory.annotation.Qualifier("lessorServiceMapper") LessorMapper lessorMapper,
            RelyingParty relyingParty,
            PasswordEncoder passwordEncoder,
            LesseeRepository lesseeRepository,
            JwtProvider jwtProvider,
            RefreshTokenService refreshTokenService
    ) {
        this.lessorRepository = lessorRepository;
        this.userRepository = userRepository;
        this.lessorMapper = lessorMapper;
        this.relyingParty = relyingParty;
        this.passwordEncoder = passwordEncoder;
        this.lesseeRepository = lesseeRepository;
        this.jwtProvider = jwtProvider;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    @Transactional
    public AuthResponseDto registerWithPassword(RegisterLessorPasswordDto request) {

        // 2. Crear UserEntity principal
        UserEntity userEntity = UserEntity.builder()
                .name(request.getName())
                .paternalSurname(request.getPaternalSurname())
                .maternalSurname(request.getMaternalSurname())
                .email(request.getEmail())
                .build();

        // 3. Crear CredentialEntity (Contraseña) vinculada al usuario
        CredentialEntity credentialEntity = CredentialEntity.builder()
                .user(userEntity)
                .credentialType(CredentialType.PASSWORD)
                .secretData(passwordEncoder.encode(request.getPassword()))
                .build();
        userEntity.getCredentials().add(credentialEntity);

        // 4. Crear Perfil LessorEntity vinculado al usuario
        LessorEntity lessorEntity = LessorEntity.builder()
                .user(userEntity)
                .phoneNumber(request.getPhoneNumber())
                .build();
        userEntity.setLessor(lessorEntity);

        // 5. Guardar. Gracias a CascadeType.ALL en UserEntity, esto guarda todo a la vez
        userRepository.save(userEntity);

        // 6. Generar tokens (Iniciar sesión de inmediato)
        String role = "ROLE_LESSOR";
        var authorities = Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority(role));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(request.getEmail(), null, authorities);

        String jwt = jwtProvider.generateToken(auth);
        String refreshToken = refreshTokenService.createRefreshToken(request.getEmail(), role).getToken();

        return new AuthResponseDto(jwt, refreshToken);
    }

    /*@Override
    public String startRegistration(CreateLessorDto dto) {
        if (lessorRepository.findByCompanyName(dto.getCompanyName()).isPresent()) {
            throw new RuntimeException("El arrendador con esta empresa ya existe.");
        }

        // Generamos un ID interno único para la identidad WebAuthn
        byte[] userHandle = new byte[32];
        new SecureRandom().nextBytes(userHandle);

        UserIdentity userIdentity = UserIdentity.builder()
                .name(dto.getCompanyName())
                .displayName(dto.getFirstName() + " " + dto.getLastName())
                .id(new ByteArray(userHandle))
                .build();

        StartRegistrationOptions startOpts = StartRegistrationOptions.builder()
                .user(userIdentity)
                .build();

        // El motor criptográfico genera el desafío y los parámetros
        PublicKeyCredentialCreationOptions options = relyingParty.startRegistration(startOpts);

        // Almacenamos el estado pendiente vinculado al nombre de la empresa
        registrationCache.put(dto.getCompanyName(), new RegistrationRequestState(dto, options));

        try {
            // Devolvemos las opciones serializadas para que la app móvil inicie el escáner de huellas
            return options.toCredentialsCreateJson();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar las opciones de registro WebAuthn", e);
        }
    }

    @Override
    public LessorResponseDto finishRegistration(VerifyLessorRegistrationDto verifyDto) {
        RegistrationRequestState pendingRequest = registrationCache.get(verifyDto.getCompanyName());
        if (pendingRequest == null) {
            throw new RuntimeException("No hay un proceso de registro activo para esta empresa o el desafío ha expirado.");
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
            CreateLessorDto originalDto = pendingRequest.originalDto;
            LessorEntity lessorEntity = new LessorEntity();
            //lessorEntity.setUserHandle(pendingRequest.options.getUser().getId().getBytes());
            //lessorEntity.setFirstName(originalDto.getFirstName());
            //lessorEntity.setLastName(originalDto.getLastName());
            //lessorEntity.setCompanyName(originalDto.getCompanyName());
            lessorEntity.setPhoneNumber(originalDto.getPhoneNumber());
            if (originalDto.getPassword() != null) {
                //lessorEntity.setPassword(passwordEncoder.encode(originalDto.getPassword()));
            }

            //PasskeyCredentialEntity credentialEntity = new PasskeyCredentialEntity();
            //credentialEntity.setCredentialId(result.getKeyId().getId().getBytes());
            //credentialEntity.setPublicKey(result.getPublicKeyCose().getBytes());
            //credentialEntity.setSignCount(result.getSignatureCount());

            //lessorEntity.addCredential(credentialEntity);

            LessorEntity savedLessorEntity = lessorRepository.save(lessorEntity);

            // 4. Limpiar la caché temporal
            registrationCache.remove(verifyDto.getCompanyName());

            return lessorMapper.toLessorResponseDto(savedLessorEntity);

        } catch (Exception e) {
            throw new RuntimeException("Fallo en la validación criptográfica de la credencial", e);
        }
    }*/

    // Clase interna para manejar el estado temporal
    private static class RegistrationRequestState {
        //final CreateLessorDto originalDto;
        final PublicKeyCredentialCreationOptions options;

        RegistrationRequestState(/*CreateLessorDto originalDto, */PublicKeyCredentialCreationOptions options) {
            //this.originalDto = originalDto;
            this.options = options;
        }
    }

    @Override
    public LessorResponseDto getLessorByCompanyName(String companyName) {
        //return lessorRepository.findByCompanyName(companyName)
        //        .map(lessorMapper::toLessorResponseDto)
        //        .orElseThrow(()-> new RuntimeException("No existe el arrendador"));
        return null;
    }

    @Override
    public LessorResponseDto getLessorByUsername(String username) {
        //return lessorRepository.findByCompanyName(username)
        //        .map(lessorMapper::toLessorResponseDto)
        //        .orElseThrow(()-> new RuntimeException("No existe el arrendador"));
        return null;
    }

    @Override
    public List<LessorResponseDto> getAllLessors() {
        return lessorRepository.findAll().stream()
                .map(lessorMapper::toLessorResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LesseeResponseDto> getFollowers(String companyName) {
        /*LessorEntity lessor = lessorRepository.findByCompanyName(companyName)
                .orElseThrow(() -> new RuntimeException("Arrendador no encontrado: " + companyName));

        return lesseeRepository.findByFollowedLessors_Id(lessor.getId()).stream()
                .map(lessee -> {
                    LesseeResponseDto dto = new LesseeResponseDto();
                    dto.setId(lessee.getId());
                    dto.setUsername(lessee.getUsername());
                    dto.setEmail(lessee.getEmail());
                    return dto;
                })
                .collect(Collectors.toList());*/
        return null;
    }
}