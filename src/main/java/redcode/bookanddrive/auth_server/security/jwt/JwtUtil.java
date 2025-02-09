package redcode.bookanddrive.auth_server.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import redcode.bookanddrive.auth_server.security.config.JwtPropertiesConfig;
import redcode.bookanddrive.auth_server.users.model.User;

@Component
public class JwtUtil {

    private final JwtPropertiesConfig jwtPropertiesConfig;

    public JwtUtil(JwtPropertiesConfig jwtPropertiesConfig) {
        this.jwtPropertiesConfig = jwtPropertiesConfig;
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtPropertiesConfig.getSecret().getBytes());
    }

    public String generateToken(User userDetails) {
        Map<String, Object> claims = new HashMap<>();
        List<String> authorities = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .toList();
        claims.put("scopes", authorities);
        claims.put("tenantName", userDetails.getTenantName());
        claims.put("tenantId", userDetails.getTenantId());

        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
            .addClaims(claims)
            .setSubject(username)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + jwtPropertiesConfig.getTokenLifespan()))
            .signWith(getSigningKey())
            .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    public String extractUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractTenantNameFromToken(String token) {
        return extractClaim(token, JwtUtil::getTenantName);
    }

    public UUID extractTenantIdFromToken(String token) {
        return UUID.fromString(extractClaim(token, JwtUtil::getTenantId));
    }

    public LocalDateTime extractExpirationDate(String token) {
        Date expirationDate = extractClaim(token, Claims::getExpiration);
        LocalDateTime localDateTime = expirationDate.toInstant()
            .atZone(ZoneId.systemDefault()) // Use system default zone
            .toLocalDateTime();
        return localDateTime;
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsernameFromToken(token);
        return username.equals(userDetails.getUsername())
            && !isTokenExpired(token);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public static String getTenantName(Claims claims) {
        return (String) claims.get("tenantName");
    }

    public static String getTenantId(Claims claims) {
        return (String) claims.get("tenantId");
    }
}
