package aleosh.online.vivia.features.auth.services.impl;

import aleosh.online.vivia.core.config.jwt.JwtProvider;
import aleosh.online.vivia.features.auth.data.dtos.request.GoogleLoginRequestDto;
import aleosh.online.vivia.features.auth.data.dtos.request.VerifyLoginDto;
import aleosh.online.vivia.features.auth.data.dtos.response.AuthResponseDto;
import aleosh.online.vivia.features.auth.data.entities.CredentialEntity;
import aleosh.online.vivia.features.auth.data.repositories.CredentialRepository;
import aleosh.online.vivia.features.auth.domain.objectvalues.CredentialType;
import aleosh.online.vivia.features.auth.services.IAuthService;
import aleosh.online.vivia.features.users.users.data.entities.UserEntity;
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
import aleosh.online.vivia.features.auth.data.entities.RefreshTokenEntity;
import aleosh.online.vivia.features.auth.data.dtos.request.LoginRequestDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthServiceImpl implements IAuthService {

    private final JwtProvider jwtProvider;
    private final RelyingParty relyingParty;
    private final UserDetailsServiceImpl userDetailsService;
    //private final PasskeyCredentialRepository passkeyRepository;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenServiceImpl refreshTokenServiceImpl;
    private final GoogleTokenVerifierServiceImpl googleTokenVerifierService;
    private final CredentialRepository credentialRepository;

    // Caché para los desafíos de login. La clave es el propio desafío en Base64Url
    private final Map<String, AssertionRequest> loginCache = new ConcurrentHashMap<>();

    public AuthServiceImpl(
            JwtProvider jwtProvider,
            RelyingParty relyingParty,
            UserDetailsServiceImpl userDetailsService,
            /*PasskeyCredentialRepository passkeyRepository,*/ 
            AuthenticationManager authenticationManager,
            RefreshTokenServiceImpl refreshTokenServiceImpl,
            GoogleTokenVerifierServiceImpl googleTokenVerifierService,
            CredentialRepository credentialRepository
    ) {
        this.jwtProvider = jwtProvider;
        this.relyingParty = relyingParty;
        this.userDetailsService = userDetailsService;
        //this.passkeyRepository = passkeyRepository;
        this.authenticationManager = authenticationManager;
        this.refreshTokenServiceImpl = refreshTokenServiceImpl;
        this.googleTokenVerifierService = googleTokenVerifierService;
        this.credentialRepository = credentialRepository;
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
                //passkeyRepository.findById...
                    //cred.setSignCount...
                    //passkeyRepository.save(cred);
                //});

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
                
                String role = userDetails.getAuthorities().iterator().next().getAuthority();
                RefreshTokenEntity refreshToken = refreshTokenServiceImpl.createRefreshToken(identifier, role);
                
                return new AuthResponseDto(jwt, refreshToken.getToken());
            } else {
                throw new RuntimeException("La validación de la huella falló.");
            }

        } catch (Exception e) {
            throw new RuntimeException("Error en el proceso de autenticación WebAuthn", e);
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
        RefreshTokenEntity refreshToken = refreshTokenServiceImpl.createRefreshToken(loginDto.getIdentifier(), role);
        
        return new AuthResponseDto(jwt, refreshToken.getToken());
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
                .orElseThrow(() -> new RuntimeException(
                    "No existe una cuenta vinculada a este Google ID. Por favor, regístrate primero."
                ));

        // Obtener el usuario y determinar el rol real desde la base de datos
        UserEntity userEntity = credentialEntity.getUser();

        String roleFromDB;
        if (userEntity.getLessor() != null) {
            roleFromDB = "ROLE_LESSOR";
        } else if (userEntity.getLessee() != null) {
            roleFromDB = "ROLE_LESSEE";
        } else {
            throw new RuntimeException("El usuario no tiene un rol asignado");
        }

        // Validar que el rol enviado por el cliente coincide con el de la BD
        if (!roleFromDB.equals(googleLoginDto.getRole())) {
            throw new RuntimeException(
                "El rol proporcionado (" + googleLoginDto.getRole() +
                ") no coincide con el rol del usuario registrado (" + roleFromDB + ")"
            );
        }

        var authorities = Collections.singletonList(new SimpleGrantedAuthority(roleFromDB));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                email, null, authorities
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = jwtProvider.generateToken(auth);
        RefreshTokenEntity refreshToken = refreshTokenServiceImpl.createRefreshToken(email, roleFromDB);

        return new AuthResponseDto(jwt, refreshToken.getToken());
    }

    @Override
    public AuthResponseDto refreshToken(RefreshTokenRequestDto request) {
        String requestRefreshToken = request.getRefreshToken();

        RefreshTokenEntity refreshTokenEntity = refreshTokenServiceImpl.findByToken(requestRefreshToken);
        refreshTokenServiceImpl.verifyExpiration(refreshTokenEntity);

        String userIdentifier = refreshTokenEntity.getUserIdentifier();
        String role = refreshTokenEntity.getRole();

        UserDetails userDetails = userDetailsService.loadUserByIdentifierAndRole(userIdentifier, role);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        String jwt = jwtProvider.generateToken(auth);

        // Rotation
        RefreshTokenEntity newRefreshToken = refreshTokenServiceImpl.createRefreshToken(userIdentifier, role);

        return new AuthResponseDto(jwt, newRefreshToken.getToken());
    }
}