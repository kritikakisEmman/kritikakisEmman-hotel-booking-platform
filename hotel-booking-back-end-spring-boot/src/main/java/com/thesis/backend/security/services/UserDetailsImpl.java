package com.thesis.backend.security.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thesis.backend.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * UserDetailsImpl - The bridge between our custom User entity and Spring Security.
 *
 * PROBLEM:
 * Spring Security does not know anything about our "User" JPA entity.
 * It works with its own interface called "UserDetails" which defines
 * what it needs to know about a user: username, password, authorities (roles),
 * and account status flags.
 *
 * SOLUTION:
 * We create this class that IMPLEMENTS the UserDetails interface,
 * effectively wrapping our User entity and translating it into
 * something Spring Security can understand.
 *
 * IMPLEMENTS UserDetails:
 * "implements" in Java means this class signs a contract:
 * it promises to provide ALL the methods defined in the UserDetails interface.
 * If any method is missing, the code will not compile.
 * The UserDetails interface is defined in spring-security-core library.
 *
 * Serializable:
 * UserDetails extends Serializable (indirectly). This means instances of this
 * class can be converted to a byte stream (e.g. for storing in a session or cache).
 * The serialVersionUID is a version ID for this serialized form.
 */
public class UserDetailsImpl implements UserDetails {
  private static final long serialVersionUID = 1L;

  private Long id;
  private String username;
  private String email;

  /**
   * @JsonIgnore
   *   When Jackson (the JSON library) serializes this object to JSON,
   *   it will SKIP this field. We never want to send the password
   *   (even hashed) in any HTTP response.
   */
  @JsonIgnore
  private String password;

  /**
   * authorities = the roles of this user, translated into Spring Security format.
   *
   * Collection<? extends GrantedAuthority>
   *   This is a Java generics wildcard type.
   *   GrantedAuthority is the Spring Security interface representing a permission.
   *   "? extends GrantedAuthority" means: a collection of anything that IS a
   *   GrantedAuthority (or a subclass of it).
   *   We use SimpleGrantedAuthority as the concrete implementation.
   */
  private Collection<? extends GrantedAuthority> authorities;

  public UserDetailsImpl(Long id, String username, String email, String password,
      Collection<? extends GrantedAuthority> authorities) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.password = password;
    this.authorities = authorities;
  }

  /**
   * Static factory method that converts our User JPA entity into a UserDetailsImpl.
   *
   * WHY static? Because you call it WITHOUT creating an instance first:
   *   UserDetailsImpl.build(user) — not new UserDetailsImpl().build(user)
   * It's a common pattern for factory/builder methods in Java.
   *
   * THE KEY CONVERSION:
   * user.getRoles() returns Set<Role> (our JPA entities)
   * Spring Security needs List<GrantedAuthority>
   *
   * The Stream API (Java 8+) does the transformation:
   * 1. user.getRoles().stream()       → creates a stream from the Set<Role>
   * 2. .map(role -> ...)              → transforms each Role into a GrantedAuthority
   * 3. role.getName()                 → returns ERole enum (e.g. ERole.ROLE_ADMIN)
   * 4. .name()                        → returns the String name of the enum ("ROLE_ADMIN")
   * 5. new SimpleGrantedAuthority(..) → wraps it in Spring's GrantedAuthority format
   * 6. .collect(Collectors.toList())  → collects results into a List
   */
  public static UserDetailsImpl build(User user) {
    List<GrantedAuthority> authorities = user.getRoles().stream()
        .map(role -> new SimpleGrantedAuthority(role.getName().name()))
        .collect(Collectors.toList());

    return new UserDetailsImpl(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        user.getPassword(),
        authorities);
  }

  /**
   * getAuthorities() - Returns the roles/permissions of this user.
   * This is what Spring Security checks when you use:
   *   @PreAuthorize("hasRole('ADMIN')")
   * Spring looks through these authorities for "ROLE_ADMIN".
   *
   * @Override means we are implementing a method defined in the UserDetails interface.
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  public Long getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  /**
   * The following four methods are account status checks required by UserDetails.
   * We return "true" for all of them because we don't implement features like
   * account locking, expiration, or credential expiration.
   * In a production system, you would check these against database fields.
   */
  @Override
  public boolean isAccountNonExpired() {
    return true; // We don't expire accounts
  }

  @Override
  public boolean isAccountNonLocked() {
    return true; // We don't lock accounts
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true; // We don't expire passwords
  }

  @Override
  public boolean isEnabled() {
    return true; // All accounts are enabled
  }

  /**
   * equals() - Compares two UserDetailsImpl objects by their ID only.
   * Used by Spring Security internally to compare user objects.
   * We override the default Object.equals() which compares memory references.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    UserDetailsImpl user = (UserDetailsImpl) o;
    return Objects.equals(id, user.id);
  }
}
