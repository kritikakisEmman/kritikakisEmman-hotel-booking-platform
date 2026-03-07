package com.thesis.backend.security;

import com.thesis.backend.security.jwt.AuthEntryPointJwt;
import com.thesis.backend.security.jwt.AuthTokenFilter;
import com.thesis.backend.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * WebSecurityConfig - The central Spring Security configuration class.
 *
 * This is the "command center" of the entire security setup.
 * All the other security classes (JwtUtils, AuthTokenFilter, etc.)
 * are wired together and configured here.
 *
 * @Configuration
 *   Tells Spring: "This class contains bean definitions."
 *   Methods annotated with @Bean inside produce objects that Spring
 *   manages in its Application Context (like a factory class).
 *
 * @EnableWebSecurity
 *   Activates Spring Security for the entire web application.
 *   Without this, none of the security rules below would apply.
 *
 * @EnableGlobalMethodSecurity(prePostEnabled = true)
 *   Enables METHOD-LEVEL security annotations in Controllers and Services.
 *   prePostEnabled = true unlocks:
 *     @PreAuthorize("hasRole('ADMIN')")  → checked BEFORE the method runs
 *     @PostAuthorize("...")              → checked AFTER the method runs
 *   Without this annotation, all @PreAuthorize annotations are IGNORED.
 *
 * EXTENDS WebSecurityConfigurerAdapter:
 *   Spring Security's base class for custom security configuration.
 *   By extending it, we override its methods to define our own rules.
 *   (Deprecated in Spring Boot 3+, but correct for Spring Boot 2.x)
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
    // securedEnabled = true,   // enables @Secured annotation (older approach)
    // jsr250Enabled = true,    // enables @RolesAllowed annotation (Java EE standard)
    prePostEnabled = true)      // enables @PreAuthorize and @PostAuthorize (recommended)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  /**
   * @Autowired — Spring automatically injects these dependencies.
   * UserDetailsServiceImpl is a @Service, AuthEntryPointJwt is a @Component.
   * Both are registered in the Application Context and injected here.
   */
  @Autowired
  UserDetailsServiceImpl userDetailsService;

  @Autowired
  private AuthEntryPointJwt unauthorizedHandler;

  /**
   * @Bean — Registers AuthTokenFilter as a Spring-managed bean.
   * We create it here (rather than using @Component on the filter class)
   * to ensure Spring Security uses the correct, fully-initialized instance
   * when building the filter chain.
   */
  @Bean
  public AuthTokenFilter authenticationJwtTokenFilter() {
    return new AuthTokenFilter();
  }

  /**
   * configure(AuthenticationManagerBuilder) - Defines HOW users are authenticated.
   *
   * This tells Spring Security:
   * 1. WHERE to look up users → userDetailsService (loads from database by username)
   * 2. HOW to verify passwords → passwordEncoder() (BCrypt comparison)
   *
   * Spring Security uses this when AuthenticationManager.authenticate() is called
   * in AuthController during the login process.
   *
   * @Override — Overriding the parent method to customize authentication setup.
   */
  @Override
  public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
    authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
  }

  /**
   * authenticationManagerBean() - Exposes AuthenticationManager as a Spring bean.
   *
   * AuthenticationManager is Spring Security's core authentication processor.
   * By default it is NOT available as a bean — we must explicitly expose it here.
   * This allows us to @Autowired it in AuthController to call authenticate()
   * during the login endpoint.
   *
   * @Bean @Override — Overriding parent method AND making it a bean.
   */
  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  /**
   * passwordEncoder() - Defines the password hashing algorithm used across the app.
   *
   * BCryptPasswordEncoder applies the BCrypt hashing function.
   * BCrypt is a ONE-WAY hash — you cannot reverse it to get the plain password.
   * It adds a random "salt" per password, so identical passwords produce
   * different hashes, preventing rainbow table attacks.
   *
   * Example:
   *   Plain password:  "myPassword123"
   *   BCrypt hash:     "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
   *
   * Used in two places:
   * 1. AuthController.signup() → encodes password before saving to DB
   * 2. configure(AuthManagerBuilder) → verifies password during login
   *
   * @Bean — Makes the encoder injectable with @Autowired anywhere in the app.
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * configure(HttpSecurity) - Defines the HTTP-level security rules.
   *
   * This is the most important method — it configures:
   * - Which URLs are public vs protected
   * - Session strategy (stateless for JWT)
   * - Error handling (401 response format)
   * - The filter chain (adds our JWT filter)
   *
   * @Override — Overriding the parent method which by default secures everything.
   */
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        // CORS: Allow requests from different origins (domains/ports).
        // Our Angular app runs on a different port than this backend,
        // so CORS must be enabled. @CrossOrigin on controllers handles the details.
        .cors().and()

        // CSRF: Disabled because we use stateless JWT (no cookies).
        // CSRF attacks target session cookies — JWT in Authorization headers
        // is not vulnerable to CSRF, so this protection is unnecessary here.
        .csrf().disable()

        // 401 Handler: When an unauthenticated request reaches a protected endpoint,
        // delegate to AuthEntryPointJwt to return a proper JSON error response
        // instead of Spring's default HTML error page.
        .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()

        // STATELESS sessions: Never create or use HTTP sessions.
        // Every request must carry its own JWT token for authentication.
        // This is the correct approach for REST APIs.
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

        // URL AUTHORIZATION RULES (evaluated in order — first match wins):
        .authorizeRequests()

        // PUBLIC: No token required for these endpoints.
        .antMatchers("/api/auth/**").permitAll()            // login, register
        .antMatchers("/api/test/**").permitAll()            // test endpoints
        .antMatchers("/api/rms/**").permitAll()             // external RMS system
        .antMatchers("/api/hotel/getHotelsPaginatedWithSearchCriteria/**").permitAll()
        .antMatchers("/api/hotel/getHotelsPaginated/**").permitAll()
        .antMatchers("/api/hotel/deleteHotels/**").permitAll()
        .antMatchers("/api/hotel/getHotelById/**").permitAll()
        .antMatchers("/api/hotel/setHotel/**").permitAll()
        .antMatchers("/api/facebook/**").permitAll()
        .antMatchers("/facebook/**").permitAll()
        .antMatchers("/api/reservation/setReservation").permitAll()

        // PROTECTED: All other endpoints require a valid JWT token.
        // No token → AuthEntryPointJwt returns 401 Unauthorized.
        // Valid token but insufficient role → @PreAuthorize returns 403 Forbidden.
        .anyRequest().authenticated();

    // Add our custom JWT filter BEFORE Spring's default login filter.
    // This ensures every request is checked for a JWT token first,
    // and the authenticated user is placed in the SecurityContext
    // before any authorization decisions are made.
    http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
  }
}
