package aleosh.online.vivia.features.users.lessor.services.impl;

import aleosh.online.vivia.features.auth.data.dtos.response.AuthResponseDto;
import aleosh.online.vivia.features.auth.services.impl.GoogleTokenVerifierServiceImpl;
import aleosh.online.vivia.features.users.lessor.data.dtos.request.RegisterLessorGoogleDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.request.RegisterLessorPasswordDto;
import aleosh.online.vivia.core.config.jwt.JwtProvider;
import aleosh.online.vivia.features.auth.data.entities.CredentialEntity;
import aleosh.online.vivia.features.auth.domain.objectvalues.CredentialType;
import aleosh.online.vivia.features.auth.services.impl.RefreshTokenServiceImpl;
import aleosh.online.vivia.features.users.users.data.entities.UserEntity;
import aleosh.online.vivia.features.users.users.data.repositories.UserRepository;
import aleosh.online.vivia.features.users.users.domain.exceptions.UserAlreadyExistsException;
import aleosh.online.vivia.features.users.users.domain.repositories.IUserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;

import aleosh.online.vivia.features.users.lessor.data.dtos.response.LessorResponseDto;
import aleosh.online.vivia.features.users.lessor.data.entities.LessorEntity;
import aleosh.online.vivia.features.users.lessor.data.repositories.LessorRepository;
import aleosh.online.vivia.features.users.lessor.services.ILessorService;
import aleosh.online.vivia.features.users.lessor.services.mappers.LessorMapper;
import com.yubico.webauthn.RelyingParty;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Collections;
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
            GoogleTokenVerifierServiceImpl googleTokenVerifierService
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
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(request.getEmail(), null, authorities);

        String jwt = jwtProvider.generateToken(auth);
        String refreshToken = refreshTokenServiceImpl.createRefreshToken(request.getEmail(), role).getToken();

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
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, null, authorities);

        String jwt = jwtProvider.generateToken(auth);
        String refreshToken = refreshTokenServiceImpl.createRefreshToken(email, role).getToken();

        return new AuthResponseDto(jwt, refreshToken);
    }

    @Override
    public LessorResponseDto getLessorByCompanyName(String companyName) {
        return null;
    }

    @Override
    public LessorResponseDto getLessorByUsername(String username) {
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
        return null;
    }
}