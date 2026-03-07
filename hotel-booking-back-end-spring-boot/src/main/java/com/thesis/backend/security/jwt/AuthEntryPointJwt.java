package com.thesis.backend.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * AuthEntryPointJwt - Handles unauthorized access attempts (HTTP 401).
 *
 * WHAT IS AN ENTRY POINT?
 * In Spring Security, an AuthenticationEntryPoint is called when a request
 * reaches a protected resource WITHOUT valid authentication credentials.
 * Think of it as the "bouncer" response: "You can't come in without ID."
 *
 * Without this class, Spring Security would return a default HTML error page
 * for 401 errors, which is useless for a REST API (Angular expects JSON).
 * This class overrides that behavior to return a proper JSON error response.
 *
 * WHEN IS IT TRIGGERED?
 * When a request hits an endpoint that requires authentication (anyRequest().authenticated())
 * but the AuthTokenFilter found no valid JWT — the SecurityContext is empty.
 * Spring Security's ExceptionTranslationFilter catches the resulting
 * AuthenticationException and delegates to this entry point.
 *
 * IMPLEMENTS AuthenticationEntryPoint:
 * AuthenticationEntryPoint is a Spring Security interface with one method:
 *   commence(request, response, authException)
 * We implement it to control exactly what the 401 response looks like.
 *
 * @Component
 *   Registers this class in the Spring Application Context as a bean,
 *   so it can be @Autowired into WebSecurityConfig.
 */
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

  private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

  /**
   * commence - Called automatically by Spring Security when authentication fails.
   *
   * We write a JSON response directly to the HttpServletResponse output stream.
   * This bypasses the normal Controller flow entirely — the response is built here.
   *
   * @param request       The request that was rejected.
   * @param response      The response to write the error to.
   * @param authException The exception that caused authentication to fail.
   *                      Its message describes WHY authentication failed.
   *
   * @Override - Implementing the single method of AuthenticationEntryPoint interface.
   */
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
      throws IOException, ServletException {

    // Log the unauthorized access attempt for debugging/monitoring.
    logger.error("Unauthorized error: {}", authException.getMessage());

    // Set the response Content-Type to JSON so the client knows what to expect.
    // MediaType.APPLICATION_JSON_VALUE = "application/json"
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    // Set the HTTP status code to 401 Unauthorized.
    // SC_UNAUTHORIZED is the constant for HTTP status 401.
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    // Build the JSON response body as a Map.
    // This Map will be converted to JSON by ObjectMapper below.
    // The response will look like:
    // {
    //   "status": 401,
    //   "error": "Unauthorized",
    //   "message": "Full authentication is required to access this resource",
    //   "path": "/api/hotel/protected-endpoint"
    // }
    final Map<String, Object> body = new HashMap<>();
    body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
    body.put("error", "Unauthorized");
    body.put("message", authException.getMessage());
    body.put("path", request.getServletPath()); // The URL that was accessed

    // ObjectMapper is Jackson's main class for converting Java objects to JSON.
    // writeValue(outputStream, object) serializes the Map to JSON and
    // writes it directly to the HTTP response body.
    final ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(response.getOutputStream(), body);
  }
}
