package aleosh.online.vivia.features.users.lessee.services.impl;

import aleosh.online.vivia.core.config.jwt.JwtProvider;
import aleosh.online.vivia.features.auth.data.dtos.response.AuthResponseDto;
import aleosh.online.vivia.features.auth.data.entities.CredentialEntity;
import aleosh.online.vivia.features.auth.domain.objectvalues.CredentialType;
import aleosh.online.vivia.features.auth.services.impl.GoogleTokenVerifierServiceImpl;
import aleosh.online.vivia.features.auth.services.impl.RefreshTokenServiceImpl;
import aleosh.online.vivia.features.users.lessee.data.dtos.request.RegisterLesseeGoogleDto;
import aleosh.online.vivia.features.users.lessee.data.dtos.request.RegisterLesseePasswordDto;
import aleosh.online.vivia.features.users.lessee.data.entities.LesseeEntity;
import aleosh.online.vivia.features.users.lessee.data.repositories.LesseeRepository;
import aleosh.online.vivia.features.users.lessee.services.ILesseeService;
import aleosh.online.vivia.features.users.users.data.entities.UserEntity;
import aleosh.online.vivia.features.users.users.data.repositories.UserRepository;
import aleosh.online.vivia.features.users.users.domain.exceptions.UserAlreadyExistsException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class LesseeServiceImpl implements ILesseeService {

    private final LesseeRepository lesseeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenServiceImpl refreshTokenServiceImpl;
    private final GoogleTokenVerifierServiceImpl googleTokenVerifierService;

    public LesseeServiceImpl(
            LesseeRepository lesseeRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtProvider jwtProvider,
            RefreshTokenServiceImpl refreshTokenServiceImpl,
            GoogleTokenVerifierServiceImpl googleTokenVerifierService
    ) {
        this.lesseeRepository = lesseeRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.refreshTokenServiceImpl = refreshTokenServiceImpl;
        this.googleTokenVerifierService = googleTokenVerifierService;
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

    /*@Override
    public String startRegistration(CreateLesseeDto createLesseeDto) {
        return null;
    }

    @Override
    public LesseeResponseDto finishRegistration(VerifyLesseeRegistrationDto verifyLesseeRegistrationDto) {
        return null;
    }

    @Override
    public LesseeResponseDto getLesseeByUsername(String username) {
        return null;
    }

    @Override
    public LesseeResponseDto getLesseeByEmail(String email) {
        return null;
    }

    @Override
    public List<LesseeResponseDto> getAllLessees() {
        return null;
    }

    @Override
    public void followLessor(String lesseeEmail, String lessorCompanyName) {
    }

    @Override
    public void updateFcmToken(String email, String fcmToken) {
    }

    @Override
    public List<LessorWithFollowStatusDto> getAllLessorsWithFollowStatus(String email) {
        return null;
    }*/
}