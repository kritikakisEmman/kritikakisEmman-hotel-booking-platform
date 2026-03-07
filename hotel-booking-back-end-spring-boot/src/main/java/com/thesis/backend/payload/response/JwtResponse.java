package com.thesis.backend.payload.response;

import java.util.List;

/**
 * JwtResponse - DTO for the successful login response.
 *
 * This is what the backend returns to the Angular frontend after a successful login.
 * The frontend stores this data in localStorage and uses it for:
 * - The token → sent as "Authorization: Bearer <token>" in every subsequent request
 * - The id, username, email → displayed in the UI (navbar, profile page)
 * - The roles → used to show/hide UI elements (e.g. admin menu only for ROLE_ADMIN)
 *
 * Example JSON response:
 * {
 *   "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiJ9...",
 *   "type": "Bearer",
 *   "id": 1,
 *   "username": "admin",
 *   "email": "admin@bookingapp.com",
 *   "roles": ["ROLE_ADMIN", "ROLE_MODERATOR"]
 * }
 *
 * PACKAGE: payload/response → data going OUT of the API.
 * This class has NO JPA annotations — it's a pure data carrier, not a database entity.
 */
public class JwtResponse {

  private String token;

  /**
   * type = "Bearer" — hardcoded default value.
   * "Bearer" is the standard token type for JWT in HTTP Authorization headers.
   * The format is: Authorization: Bearer <token>
   * The Angular interceptor (auth.interceptor.ts) uses this to build the header.
   */
  private String type = "Bearer";

  private Long id;
  private String username;
  private String email;

  /**
   * roles — List of role names as Strings (e.g. ["ROLE_ADMIN", "ROLE_USER"]).
   * Comes from UserDetailsImpl.getAuthorities() mapped to their String names.
   * The Angular frontend checks this list to decide what to show in the UI.
   */
  private List<String> roles;

  /**
   * Constructor — the ONLY way to create a JwtResponse.
   * No default constructor → you must provide all values when creating it.
   * Called in AuthController.signin() after successful authentication.
   */
  public JwtResponse(String accessToken, Long id, String username, String email, List<String> roles) {
    this.token = accessToken;
    this.id = id;
    this.username = username;
    this.email = email;
    this.roles = roles;
  }

  // Getters and setters — required by Jackson to serialize this object to JSON.
  // Jackson calls getAccessToken() to get the "accessToken" field in the JSON.
  // Note: the getter is named getAccessToken() but the field is "token" —
  // Jackson uses the GETTER NAME (minus "get") as the JSON key: "accessToken".
  public String getAccessToken() {
    return token;
  }

  public void setAccessToken(String accessToken) {
    this.token = accessToken;
  }

  public String getTokenType() {
    return type;
  }

  public void setTokenType(String tokenType) {
    this.type = tokenType;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public List<String> getRoles() {
    return roles;
  }
}
