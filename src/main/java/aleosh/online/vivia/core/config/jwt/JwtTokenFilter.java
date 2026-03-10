package aleosh.online.vivia.core.config.jwt;

import aleosh.online.vivia.features.auth.services.impl.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final static Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. Obtenemos el token de la solicitud (del Header "Authorization")
            String token = getTokenFromRequest(request);

            // 2. Validamos el token
            if (token != null && jwtProvider.validateToken(token)) {

                // 3. Obtenemos el username del token
                String username = jwtProvider.getUsernameFromToken(token);

                // 4. Cargamos los detalles del usuario desde la base de datos
                // (Esto asegura que el usuario siga existiendo y tenga roles actualizados)
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 5. Creamos la autenticación
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // 6. Establecemos la autenticación en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            logger.error("No se pudo establecer la autenticación del usuario: {}", e.getMessage());
        }

        // 7. IMPORTANTE: Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token del header "Authorization: Bearer <token>"
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7); // Quita "Bearer " (7 caracteres)
        }
        return null;
    }
}
