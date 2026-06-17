package aleosh.online.vivia.features.auth.services.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        throw new UsernameNotFoundException("Not implemented yet");
    }

    public UserDetails loadUserByIdentifierAndRole(String identifier, String role) {
        throw new RuntimeException("Not implemented yet");
    }
}
