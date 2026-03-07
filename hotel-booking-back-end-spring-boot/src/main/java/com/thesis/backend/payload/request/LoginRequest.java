package com.thesis.backend.payload.request;

import javax.validation.constraints.NotBlank;

/**
 * LoginRequest - DTO (Data Transfer Object) for the login endpoint.
 *
 * WHAT IS A DTO?
 * A DTO is a simple Java class whose only purpose is to carry data between layers.
 * It has no business logic — only fields, getters, and setters.
 *
 * WHY not use the User entity directly as the request body?
 * The User entity is coupled to the database (JPA annotations, relationships, etc.)
 * We don't want to expose all User fields in the API.
 * LoginRequest contains ONLY what we need: username + password.
 * This keeps the API clean, secure, and decoupled from the database layer.
 *
 * WHERE IS IT USED?
 * AuthController.signin() receives it as @RequestBody.
 * Spring (via Jackson library) automatically deserializes the incoming JSON:
 *   { "username": "admin", "password": "myPassword" }
 * into a LoginRequest object before the method is called.
 *
 * PACKAGE:
 * payload/request → data coming INTO the API (request payloads)
 * payload/response → data going OUT of the API (response payloads)
 */
public class LoginRequest {

  /**
   * @NotBlank
   *   Bean Validation constraint from javax.validation (JSR-380 standard).
   *   Ensures the field is not null, not empty (""), and not just whitespace ("   ").
   *   Activated by @Validated on the controller method parameter.
   *   If validation fails → Spring automatically returns HTTP 400 Bad Request.
   *   You don't need any if-statement to check this manually.
   */
  @NotBlank
  private String username;

  @NotBlank
  private String password;

  // Standard getters and setters.
  // Jackson uses these to deserialize JSON → Java object and serialize Java → JSON.
  // Without getters, Jackson cannot read the field values when serializing the response.
  // Without setters, Jackson cannot populate the fields when deserializing the request.
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
