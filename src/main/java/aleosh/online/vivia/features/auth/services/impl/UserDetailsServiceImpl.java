package aleosh.online.vivia.features.auth.services.impl;

import aleosh.online.vivia.features.users.lessee.domain.entities.Lessee;
import aleosh.online.vivia.features.users.lessee.domain.repositories.ILesseeRepository;
import aleosh.online.vivia.features.users.lessor.domain.entities.Lessor;
import aleosh.online.vivia.features.users.lessor.domain.repositories.ILessorRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {

    private final ILessorRepository lessorRepository;
    private final ILesseeRepository lesseeRepository;

    public UserDetailsServiceImpl(ILessorRepository lessorRepository, ILesseeRepository lesseeRepository) {
        this.lessorRepository = lessorRepository;
        this.lesseeRepository = lesseeRepository;
    }

    // Usado por Spring temporalmente, infiere el usuario basándose en si contiene un '@'
    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        if (identifier.contains("@")) {
            return loadLesseeByEmail(identifier);
        }
        return loadLessorByCompanyName(identifier);
    }

    // Nuevo método usado por nuestro JwtTokenFilter
    public UserDetails loadUserByIdentifierAndRole(String identifier, String role) throws UsernameNotFoundException {
        if ("ROLE_LESSEE".equals(role)) {
            return loadLesseeByEmail(identifier);
        } else if ("ROLE_LESSOR".equals(role)) {
            return loadLessorByCompanyName(identifier);
        }
        throw new UsernameNotFoundException("Rol no reconocido en el token: " + role);
    }

    private UserDetails loadLessorByCompanyName(String companyName) {
        Lessor lessor = lessorRepository.getByCompanyName(companyName)
                .orElseThrow(() -> new UsernameNotFoundException("Lessor no encontrado: " + companyName));

        return new org.springframework.security.core.userdetails.User(
                lessor.getCompanyName(), // Se usa companyName como identificador
                lessor.getPassword() != null ? lessor.getPassword() : "", // Contraseña
                List.of(new SimpleGrantedAuthority("ROLE_LESSOR"))
        );
    }

    private UserDetails loadLesseeByEmail(String email) {
        Lessee lessee = lesseeRepository.getByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Lessee no encontrado: " + email));

        return new org.springframework.security.core.userdetails.User(
                lessee.getEmail(), // Se usa email como identificador
                lessee.getPassword() != null ? lessee.getPassword() : "", // Contraseña
                List.of(new SimpleGrantedAuthority("ROLE_LESSEE"))
        );
    }
}