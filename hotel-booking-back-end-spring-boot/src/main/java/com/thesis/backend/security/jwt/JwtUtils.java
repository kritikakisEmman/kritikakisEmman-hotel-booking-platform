package com.thesis.backend.security.jwt;

import com.thesis.backend.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * JwtUtils - Utility class for creating, parsing, and validating JWT tokens.
 *
 * WHAT IS A JWT (JSON Web Token)?
 * A JWT is a compact, self-contained string that carries information (claims)
 * about a user. It has 3 parts separated by dots:
 *
 *   eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiJ9.SflKxwRJSMeKKF2QT4fwpMeJf36
 *   |___ HEADER _______| |____ PAYLOAD _______| |_______ SIGNATURE __________|
 *
 * HEADER:  Algorithm used for signing (e.g. HS256 = HMAC with SHA-256)
 * PAYLOAD: The actual data — username (sub), issued time, expiration time
 * SIGNATURE: HEADER + PAYLOAD signed with the secret key.
 *            If anyone tampers with the token, the signature won't match.
 *
 * WHY JWT instead of sessions?
 * Sessions require the server to store state (who is logged in).
 * JWT is STATELESS — the server doesn't store anything. Every request
 * carries the proof of identity (the token) and the server just validates it.
 * This scales much better in distributed systems.
 *
 * @Component
 *   Registers this class in Spring's Application Context as a bean.
 *   It can then be @Autowired anywhere in the application.
 *   @Component is the generic stereotype. For more specific use cases:
 *   @Service (business logic), @Repository (data access), @Controller (web layer).
 */
@Component
public class JwtUtils {

  /**
   * Logger - used for logging error messages when token validation fails.
   * SLF4J (Simple Logging Facade for Java) is a logging abstraction.
   * LoggerFactory.getLogger() creates a logger named after this class.
   * In production, logs go to files or monitoring services.
   */
  private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

  /**
   * @Value("${backend.app.jwtSecret}")
   *   Spring reads this value from application.properties:
   *     backend.app.jwtSecret=mySecretKey
   *   At startup, Spring injects the value into this field.
   *   This keeps sensitive configuration OUT of the source code.
   *   In production (Railway), this comes from environment variables.
   */
  @Value("${backend.app.jwtSecret}")
  private String jwtSecret;

  /**
   * Token expiration time in milliseconds.
   * Example: 86400000 ms = 86400 seconds = 1440 minutes = 24 hours.
   * After this time, the token is considered expired and the user
   * must log in again to get a new one.
   */
  @Value("${backend.app.jwtExpirationMs}")
  private int jwtExpirationMs;

  /**
   * Creates a cryptographic signing key from the jwtSecret string.
   * Keys.hmacShaKeyFor() converts the raw bytes of the secret string
   * into a Key object compatible with HMAC-SHA algorithms.
   * This key is used to SIGN tokens when creating them and to
   * VERIFY the signature when validating them.
   */
  private Key getSigningKey() {
    return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * generateJwtToken - Creates a new JWT token after successful authentication.
   *
   * Called by AuthController.signin() immediately after the user is authenticated.
   *
   * @param authentication The Authentication object created by Spring Security
   *   after successful login. It contains the authenticated user's details.
   *
   * HOW THE TOKEN IS BUILT (using the JJWT library builder pattern):
   * .setSubject()    → the "sub" claim: who this token is for (username)
   * .setIssuedAt()   → the "iat" claim: when was this token created
   * .setExpiration() → the "exp" claim: when does this token expire
   * .signWith()      → signs the token with our secret key (HMAC-SHA)
   * .compact()       → builds the final "xxxxx.yyyyy.zzzzz" string
   *
   * NOTE: We only store the USERNAME in the token, not the roles.
   * Roles are fetched fresh from the database on every request
   * (in UserDetailsServiceImpl.loadUserByUsername), so if you
   * revoke a role, it takes effect immediately on the next request.
   */
  public String generateJwtToken(Authentication authentication) {
    // getPrincipal() returns the currently authenticated user.
    // We cast it to UserDetailsImpl because that's what we put in
    // during authentication (see UserDetailsServiceImpl).
    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

    return Jwts.builder()
        .setSubject((userPrincipal.getUsername()))
        .setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
        .signWith(getSigningKey())
        .compact();
  }

  /**
   * getUserNameFromJwtToken - Extracts the username from a valid JWT token.
   *
   * Used in AuthTokenFilter to identify WHO is making the request.
   * The JJWT library parses the token, verifies the signature,
   * and returns the Claims (payload). getSubject() returns the "sub" claim
   * which we set to the username in generateJwtToken().
   *
   * @param token The raw JWT string from the Authorization header.
   * @return The username stored inside the token.
   */
  public String getUserNameFromJwtToken(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  /**
   * validateJwtToken - Checks if a JWT token is valid.
   *
   * A token is valid if:
   * 1. It was signed with our secret key (not tampered with)
   * 2. It has not expired
   * 3. It is well-formed (correct format)
   *
   * The JJWT library throws specific exceptions for each failure case.
   * We catch them all, log the reason, and return false.
   * The calling code (AuthTokenFilter) then skips authentication for that request.
   *
   * @param authToken The raw JWT string to validate.
   * @return true if valid, false if invalid/expired/tampered.
   */
  public boolean validateJwtToken(String authToken) {
    try {
      // If this doesn't throw an exception, the token is valid.
      Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
      return true;
    } catch (SignatureException e) {
      // Token was modified after signing — someone tried to tamper with it.
      logger.error("Invalid JWT signature: {}", e.getMessage());
    } catch (MalformedJwtException e) {
      // Token doesn't have the correct 3-part structure.
      logger.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      // Token's expiration date has passed — user must log in again.
      logger.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      // Token uses an algorithm or format we don't support.
      logger.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      // Token string is null or empty.
      logger.error("JWT claims string is empty: {}", e.getMessage());
    }

    return false;
  }
}
