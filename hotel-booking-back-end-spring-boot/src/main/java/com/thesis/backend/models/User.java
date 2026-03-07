package com.thesis.backend.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * User - JPA Entity that represents the "users" table in the database.
 *
 * DATABASE TABLE: "users"
 * +----+---------------------------+----------+----------+
 * | id | email                     | password | username |
 * +----+---------------------------+----------+----------+
 * |  1 | admin@bookingapp.com      | $2a$10.. | admin    |
 * |  2 | john@hotels.com           | $2a$10.. | john     |
 * +----+---------------------------+----------+----------+
 *
 * Additionally, a JOIN TABLE "user_roles" is automatically created:
 * +---------+---------+
 * | user_id | role_id |
 * +---------+---------+
 * |       1 |       1 |  (admin has ROLE_ADMIN)
 * |       2 |       3 |  (john has ROLE_USER)
 * +---------+---------+
 *
 * LOMBOK ANNOTATIONS:
 * @Getter / @Setter are from the Lombok library.
 * Lombok is a code-generation tool that automatically generates
 * getters and setters at compile time, so you don't have to write them.
 * However, in this class some getters/setters are still written manually
 * because they were added before Lombok was configured.
 */
@Entity
@Getter
@Setter
@Table(name = "users",
    uniqueConstraints = {
        // These constraints create UNIQUE indexes in MySQL,
        // preventing two users with the same username or email.
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
    })
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * @NotBlank
   *   A Bean Validation (JSR-380) constraint from javax.validation.
   *   Ensures the field is not null, not empty, and not just whitespace.
   *   Spring validates this automatically when @Valid or @Validated
   *   is used on a request body in a Controller.
   *
   * @Size(max = 120)
   *   Limits the length of the value. Also maps to VARCHAR(120) in MySQL
   *   when Hibernate creates the table.
   */
  @NotBlank
  @Size(max = 120)
  private String username;

  @NotBlank
  @Size(max = 50)
  @Email // Validates that the string has a valid email format (contains @, domain, etc.)
  private String email;

  @NotBlank
  @Size(max = 140)
  // NOTE: Passwords are stored HASHED using BCrypt, never in plain text.
  // BCrypt hashes are always 60 characters, but we use 140 as a safe max.
  private String password;

  /**
   * @ManyToMany
   *   Defines the relationship: one User can have many Roles,
   *   and one Role can belong to many Users.
   *   This requires a JOIN TABLE in the database ("user_roles").
   *
   * fetch = FetchType.LAZY
   *   Tells Hibernate: do NOT load the roles automatically every time
   *   you load a User. Load them only when explicitly accessed (roles getter).
   *   This avoids unnecessary database queries and improves performance.
   *   The opposite is FetchType.EAGER which loads everything immediately.
   *
   * @JoinTable
   *   Configures the join table that connects users and roles.
   *   name = "user_roles" → the name of the join table in MySQL
   *   joinColumns → the column in "user_roles" that references THIS entity (User)
   *   inverseJoinColumns → the column that references the OTHER entity (Role)
   *
   * Result in the database:
   *   Table "user_roles" with columns: user_id (FK to users) | role_id (FK to roles)
   */
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "user_roles",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();

  /**
   * Default no-arg constructor required by JPA/Hibernate.
   * Hibernate uses reflection to instantiate entities when reading from DB.
   */
  public User() {
  }

  /**
   * Convenience constructor used in AuthController when registering a new user.
   * Note: password must already be BCrypt-encoded before passing it here.
   */
  public User(String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Set<Role> getRoles() {
    return roles;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }
}
