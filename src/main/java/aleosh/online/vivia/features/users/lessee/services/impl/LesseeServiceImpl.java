package aleosh.online.vivia.features.users.lessee.services.impl;

import aleosh.online.vivia.core.config.jwt.JwtProvider;
import aleosh.online.vivia.features.auth.data.dtos.response.AuthResponseDto;
import aleosh.online.vivia.features.auth.data.entities.CredentialEntity;
import aleosh.online.vivia.features.auth.domain.objectvalues.CredentialType;
import aleosh.online.vivia.features.auth.services.impl.RefreshTokenService;
import aleosh.online.vivia.features.users.lessee.data.dtos.request.CreateLesseeDto;
import aleosh.online.vivia.features.users.lessee.data.dtos.request.RegisterLesseePasswordDto;
import aleosh.online.vivia.features.users.lessee.data.dtos.request.VerifyLesseeRegistrationDto;
import aleosh.online.vivia.features.users.lessee.data.dtos.response.LesseeResponseDto;
import aleosh.online.vivia.features.users.lessee.data.dtos.response.LessorWithFollowStatusDto;
import aleosh.online.vivia.features.users.lessee.data.entities.LesseeEntity;
import aleosh.online.vivia.features.users.lessee.data.repositories.LesseeRepository;
import aleosh.online.vivia.features.users.lessee.services.ILesseeService;
import aleosh.online.vivia.features.users.lessee.services.mappers.LesseeMapper;
import aleosh.online.vivia.features.users.lessor.data.repositories.LessorRepository;
import aleosh.online.vivia.features.users.lessor.services.mappers.LessorMapper;
import aleosh.online.vivia.features.users.users.data.entities.UserEntity;
import aleosh.online.vivia.features.users.users.data.repositories.UserRepository;
import aleosh.online.vivia.features.users.users.domain.repositories.IUserRepository;
import com.yubico.webauthn.RelyingParty;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class LesseeServiceImpl implements ILesseeService {

    private final LesseeRepository lesseeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    public LesseeServiceImpl(
            LesseeRepository lesseeRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtProvider jwtProvider,
            RefreshTokenService refreshTokenService
    ) {
        this.lesseeRepository = lesseeRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.refreshTokenService = refreshTokenService;
    }



    @Override
    public AuthResponseDto registerWithPassword(RegisterLesseePasswordDto request) {
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
        String refreshToken = refreshTokenService.createRefreshToken(request.getEmail(), role).getToken();

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