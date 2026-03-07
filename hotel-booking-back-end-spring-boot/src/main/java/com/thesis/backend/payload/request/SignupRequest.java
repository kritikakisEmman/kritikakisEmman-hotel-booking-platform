package com.thesis.backend.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * SignupRequest - DTO (Data Transfer Object) for the registration endpoint.
 *
 * Represents the JSON body expected by POST /api/auth/signup:
 * {
 *   "username": "john_hotel",
 *   "email":    "john@hotels.com",
 *   "password": "securePass123",
 *   "role":     ["mod"]           ← optional: "admin", "mod", or "user"
 * }
 *
 * @Getter / @Setter (Lombok library)
 *   Lombok automatically GENERATES all getter/setter methods at COMPILE TIME.
 *   The compiled .class file will have all the methods even though you don't
 *   see them written here. This eliminates boilerplate code.
 *   You can see the generated methods in the IDE by looking at the class outline.
 *   Note: Some methods are also written manually below — Lombok skips generating
 *   a method if it already exists.
 */
@Getter
@Setter
public class SignupRequest {

  /**
   * @NotBlank — Field must not be null, empty, or whitespace.
   * @Size(min = 6, max = 120) — Username length enforced at API level.
   *   The User entity also has @Size(max = 120), which maps to VARCHAR(120) in MySQL.
   */
  @NotBlank
  @Size(min = 6, max = 120)
  private String username;

  /**
   * @Email — Validates that the value looks like a valid email address.
   *   Checks for "@" and a domain. Does NOT verify if the email actually exists.
   * @Size(max = 50) — Email max 50 characters (maps to VARCHAR(50) in User entity).
   */
  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  /**
   * role — The desired role(s) for the new user. Optional field.
   * Set<String> because JSON arrays map to Java Sets.
   * Accepted values: "admin", "mod", "user" (or omitted → defaults to ROLE_USER).
   * No @NotBlank here — null is valid and means "default role".
   * See AuthController.registerUser() for the full role assignment logic.
   */
  private Set<String> role;

  /**
   * @Size(min = 6, max = 140) — Password length before hashing.
   *   After BCrypt hashing, the stored value is always ~60 chars.
   *   The min = 6 enforces a minimum password length for security.
   */
  @NotBlank
  @Size(min = 6, max = 140)
  private String password;

  // NOTE: The commented-out fields below were from an earlier design
  // where hotel info was stored directly on the User.
  // Now hotels are separate entities linked to users.
//  @NotBlank
//  @Size(max = 120)
//  private String hotel_name;
//  @NotNull
//  private int number_of_rooms;

  // Manual getters and setters.
  // These coexist with Lombok's @Getter/@Setter — Lombok won't generate
  // a method if it already exists with the same signature.
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  // setEmail was commented out — Lombok's @Setter generates it automatically.
  //public void setEmail(String email) {
  //  this.email = email;
  //}

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Set<String> getRole() {
    return this.role;
  }

  public void setRole(Set<String> role) {
    this.role = role;
  }
}
