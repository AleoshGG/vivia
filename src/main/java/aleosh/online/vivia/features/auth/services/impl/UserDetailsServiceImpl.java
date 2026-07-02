package aleosh.online.vivia.features.auth.services.impl;

import aleosh.online.vivia.core.config.security.CustomUserDetails;
import aleosh.online.vivia.features.auth.data.entities.CredentialEntity;
import aleosh.online.vivia.features.auth.data.repositories.CredentialRepository;
import aleosh.online.vivia.features.auth.domain.objectvalues.CredentialType;
import aleosh.online.vivia.features.users.lessor.data.entities.LessorEntity;
import aleosh.online.vivia.features.users.users.data.entities.UserEntity;
import aleosh.online.vivia.features.users.users.data.repositories.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final CredentialRepository credentialRepository;
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(CredentialRepository credentialRepository, UserRepository userRepository) {
        this.credentialRepository = credentialRepository;
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CredentialEntity credential = credentialRepository.findByUserEmailAndCredentialType(username, CredentialType.PASSWORD)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + username));

        UserEntity user = credential.getUser();
        String role = "ROLE_USER";
        boolean accountNonLocked = true;

        if (user.getLessor() != null) {
            role = "ROLE_LESSOR";
            LessorEntity lessor = user.getLessor();
            if ("SUSPENDED".equals(lessor.getAccountStatus())) {
                accountNonLocked = false;
            }
        } else if (user.getLessee() != null) {
            role = "ROLE_LESSEE";
        } else if (user.getAdmin() != null) {
            role = "ROLE_ADMIN";
        }

        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                credential.getSecretData(),
                Collections.singletonList(new SimpleGrantedAuthority(role)),
                accountNonLocked
        );
    }

    public UserDetails loadUserByIdentifierAndRole(String identifier, String role) {
        UserEntity user = userRepository.findByEmail(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + identifier));

        return new CustomUserDetails(
                user.getId(),
                identifier,
                "",
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );
    }
}
