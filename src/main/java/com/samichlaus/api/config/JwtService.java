package com.samichlaus.api.config;

import com.samichlaus.api.exception.InvalidCredentialsException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private static final SecretKey key = Jwts.SIG.HS512.key().build();

  public String extractUsername(String token) throws InvalidCredentialsException {
    return extractClaim(token, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver)
      throws InvalidCredentialsException {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    return Jwts.builder()
        .claims(extraClaims)
        .subject(userDetails.getUsername())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + 1000 * 3600)) // 1 hour expiration
        .signWith(key)
        .compact();
  }

  public boolean isTokenValid(String token, UserDetails userDetails)
      throws InvalidCredentialsException {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  public boolean isTokenExpired(String token) throws InvalidCredentialsException {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) throws InvalidCredentialsException {
    return extractClaim(token, Claims::getExpiration);
  }

  private Claims extractAllClaims(String token) throws InvalidCredentialsException {
    try {
      return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    } catch (Exception ex) {
      throw new InvalidCredentialsException("Token is expired");
    }
  }
}
