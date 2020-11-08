package br.com.agendasus.auth.v1.infrastructure.security;

import br.com.agendasus.auth.v1.domain.usecase.exceptions.AuthTokenException;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.function.Function;

import static br.com.agendasus.auth.v1.infrastructure.security.Constants.*;

@Component
public class TokenProvider implements Serializable {

    private static final String TOKEN_TYPE = "TokenType";

    public String getUsernameFromToken(String token) {
        String username;
        try {
            username = getClaimFromToken(token, Claims::getSubject);
        } catch (IllegalArgumentException e) {
            throw new AuthTokenException(e, "It was not possible to get the user from token");
        } catch (ExpiredJwtException e) {
            throw new AuthTokenException(e, "Token expired");
        } catch(SignatureException e) {
            throw new AuthTokenException(e, "Invalid login or password");
        }
        return username;
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SIGNING_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(String userLogin, String authorities, Integer timeoutInMinutes) {
        return TOKEN_PREFIX + Jwts.builder()
                .setSubject(userLogin)
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(SignatureAlgorithm.HS256, SIGNING_KEY)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + timeoutInMinutes * 60 * 1000))
                .compact();
    }

}
