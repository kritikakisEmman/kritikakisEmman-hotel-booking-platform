package com.thesis.backend.security.jwt;

import com.thesis.backend.security.services.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * AuthTokenFilter - Intercepts every HTTP request and authenticates it using JWT.
 *
 * WHAT IS A FILTER?
 * In Java web applications (Servlet API), a Filter is a component that sits
 * between the client and the servlet (Controller). It can inspect, modify,
 * or reject requests BEFORE they reach the Controller.
 * Think of it like airport security — every passenger (request) must pass
 * through security (filter) before boarding the plane (Controller).
 *
 * EXTENDS OncePerRequestFilter:
 * OncePerRequestFilter is a Spring base class that guarantees this filter
 * executes EXACTLY ONCE per request. Without it, some servlet containers
 * can call filters multiple times (e.g. during internal forwards/includes).
 * We extend it and implement doFilterInternal() instead of raw doFilter().
 *
 * WHERE IT FITS IN THE CHAIN:
 * Spring Security has a chain of filters. This filter is added BEFORE
 * UsernamePasswordAuthenticationFilter (see WebSecurityConfig).
 * So before Spring tries its own authentication, we check for a JWT.
 *
 * FULL FLOW PER REQUEST:
 * 1. Request arrives with "Authorization: Bearer eyJ..."
 * 2. This filter reads the JWT from the header
 * 3. Validates the JWT (not expired, not tampered)
 * 4. Loads the user from the database
 * 5. Sets the user in the SecurityContext (Spring's per-request memory)
 * 6. Passes the request to the next filter in the chain
 */
public class AuthTokenFilter extends OncePerRequestFilter {

  /**
   * @Autowired — Spring injects these dependencies automatically.
   * Note: Even though this class has no @Component annotation,
   * Spring manages it because it's created as a @Bean in WebSecurityConfig.
   */
  @Autowired
  private JwtUtils jwtUtils;

  @Autowired
  private UserDetailsServiceImpl userDetailsService;

  private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

  /**
   * doFilterInternal - The core method executed for every HTTP request.
   *
   * This method MUST call filterChain.doFilter() at the end, otherwise
   * the request will be stuck here and never reach the Controller.
   * Even if authentication fails, we call doFilter() and let downstream
   * Spring Security filters decide what to do (return 401, etc.).
   *
   * @param request     The incoming HTTP request (headers, body, URL, etc.)
   * @param response    The HTTP response we can write to.
   * @param filterChain The remaining filters. We call doFilter() to continue.
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      // Step 1: Extract the JWT string from the Authorization header.
      // Returns null if the header is missing or malformed.
      String jwt = parseJwt(request);

      // Step 2: If a token exists AND passes validation...
      if (jwt != null && jwtUtils.validateJwtToken(jwt)) {

        // Step 3: Extract the username from the token payload (the "sub" claim).
        String username = jwtUtils.getUserNameFromJwtToken(jwt);

        // Step 4: Load the full user from the database, including their roles.
        // We reload from DB every request so that role changes take effect immediately.
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Step 5: Create Spring Security's Authentication object.
        // UsernamePasswordAuthenticationToken(principal, credentials, authorities):
        //   principal   = the UserDetails object (who is the user)
        //   credentials = null (password not needed after JWT validation)
        //   authorities = the user's roles (ROLE_ADMIN, ROLE_USER, etc.)
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());

        // Attach extra request details (IP address, session ID) for auditing.
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Step 6: Store the authentication in the SecurityContext.
        // SecurityContextHolder uses a ThreadLocal — this data is available
        // ONLY for the current request thread and is cleared after the response.
        // After this line, anywhere in the application you can call:
        //   SecurityContextHolder.getContext().getAuthentication()
        // and get the current user.
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
      // If jwt is null or invalid: no authentication is set.
      // The SecurityContext remains empty for this request.
      // Spring Security will return 401 for protected endpoints.

    } catch (Exception e) {
      // Log the error but don't crash — let the request proceed unauthenticated.
      logger.error("Cannot set user authentication: {}", e);
    }

    // Step 7: ALWAYS pass to the next filter. Without this, the request stops here.
    filterChain.doFilter(request, response);
  }

  /**
   * parseJwt - Extracts the JWT string from the HTTP Authorization header.
   *
   * Standard Bearer token header format:
   *   Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIi...
   *
   * We verify:
   * 1. The header exists and is not blank (StringUtils.hasText)
   * 2. It starts with "Bearer " (the standard JWT prefix)
   * Then we remove the "Bearer " prefix (7 characters) and return the token.
   *
   * @param request The HTTP request to extract the header from.
   * @return The JWT string, or null if the header is missing or malformed.
   */
  private String parseJwt(HttpServletRequest request) {
    String headerAuth = request.getHeader("Authorization");

    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
      // substring(7) skips the first 7 characters: "Bearer "
      return headerAuth.substring(7, headerAuth.length());
    }

    return null;
  }
}
