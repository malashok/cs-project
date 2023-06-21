package server;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

public class JWT {
    private static Key apiKey  = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public static String createJwt(String username) {
        return Jwts.builder().setSubject(username).signWith(apiKey).compact();
    }

    public static String takeNameFromJwt(String jwt) {
        return Jwts.parserBuilder().setSigningKey(apiKey).build().parseClaimsJws(jwt).getBody().getSubject();
    }
}
