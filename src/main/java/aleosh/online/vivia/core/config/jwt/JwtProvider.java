package aleosh.online.vivia.core.config.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    private final static Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    // Estas variables deben ir en tu application.properties
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private int expiration;

    /**
     * Genera el token basado en la autenticación del usuario
     */
    public String generateToken(Authentication authentication) {
        // Obtenemos el usuario principal (UserDetails)
        UserDetails mainUser = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(mainUser.getUsername()) // El "subject" es el username/email
                .setIssuedAt(new Date()) // Fecha de creación
                .setExpiration(new Date(new Date().getTime() + expiration * 1000L)) // Fecha de expiración
                .signWith(getSecretKey()) // Firmamos con nuestra clave secreta
                .compact();
    }

    /**
     * Obtiene el username (email) desde el token
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Valida si el token es correcto y no ha expirado
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Token mal formado");
        } catch (UnsupportedJwtException e) {
            logger.error("Token no soportado");
        } catch (ExpiredJwtException e) {
            logger.error("Token expirado");
        } catch (IllegalArgumentException e) {
            logger.error("Token vacío");
        } catch (SignatureException e) {
            logger.error("Firma no válida (Fail en la firma)");
        }
        return false;
    }

    // Método auxiliar para decodificar la clave secreta
    private Key getSecretKey() {
        // Decodificamos la clave en Base64 para usarla en el algoritmo HMAC
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
