package aleosh.online.vivia.features.auth.services.impl;

import aleosh.online.vivia.features.auth.data.entities.CredentialEntity;
import aleosh.online.vivia.features.auth.data.repositories.CredentialRepository;
import aleosh.online.vivia.features.auth.domain.objectvalues.CredentialType;
import aleosh.online.vivia.features.users.users.data.entities.UserEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final CredentialRepository credentialRepository;

    public UserDetailsServiceImpl(CredentialRepository credentialRepository) {
        this.credentialRepository = credentialRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CredentialEntity credential = credentialRepository.findByUserEmailAndCredentialType(username, CredentialType.PASSWORD)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + username));

        UserEntity user = credential.getUser();
        String role = "ROLE_USER";

        if (user.getLessor() != null) {
            role = "ROLE_LESSOR";
        } else if (user.getLessee() != null) {
            role = "ROLE_LESSEE";
        }

        return new User(
                user.getEmail(),
                credential.getSecretData(),
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );
    }

    public UserDetails loadUserByIdentifierAndRole(String identifier, String role) {
        CredentialEntity credential = credentialRepository.findByUserEmailAndCredentialType(identifier, CredentialType.PASSWORD)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + identifier));

        return new User(
                identifier,
                credential.getSecretData(),
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );
    }
}
