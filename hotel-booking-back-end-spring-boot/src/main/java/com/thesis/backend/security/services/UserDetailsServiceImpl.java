package com.thesis.backend.security.services;

import com.thesis.backend.models.User;
import com.thesis.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserDetailsServiceImpl - Tells Spring Security HOW to load a user from the database.
 *
 * CONTEXT:
 * When a login request arrives, Spring Security needs to:
 * 1. Find the user by username in the database
 * 2. Compare the provided password with the stored (hashed) password
 * 3. Load the user's roles/authorities
 *
 * Spring Security does step 2 and 3 automatically, but it needs YOU to provide
 * step 1: "given a username, give me the user's details."
 * That's exactly what this class does.
 *
 * IMPLEMENTS UserDetailsService:
 * UserDetailsService is a Spring Security interface with ONE method:
 *   UserDetails loadUserByUsername(String username)
 * By implementing it and annotating with @Service, Spring Security
 * automatically discovers and uses this class for authentication.
 *
 * @Service
 *   Tells Spring: "This is a service class — add it to the Application Context."
 *   It's the same as @Component but more descriptive — it signals that
 *   this class contains business/application logic.
 *   Spring will create ONE instance of it and reuse it everywhere (@Autowired).
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  /**
   * @Autowired
   *   Spring automatically injects (provides) an instance of UserRepository here.
   *   You don't need to write: userRepository = new UserRepository()
   *   Spring finds the UserRepository bean in its Application Context
   *   and injects it. This is called Dependency Injection (DI).
   */
  @Autowired
  UserRepository userRepository;

  /**
   * loadUserByUsername - The ONE method required by UserDetailsService interface.
   *
   * This method is called AUTOMATICALLY by Spring Security during login.
   * You NEVER call this method manually — Spring calls it when processing
   * authentication via AuthenticationManager.authenticate().
   *
   * @Override
   *   Signals that we are implementing the method from the interface.
   *   The compiler will give an error if the method signature doesn't match.
   *
   * @Transactional
   *   CRITICAL: This annotation is needed because User has a LAZY-loaded
   *   relationship: @ManyToMany(fetch = FetchType.LAZY) for roles.
   *   Without @Transactional, the database session would close after
   *   finding the user, and when Hibernate tries to lazily load the roles,
   *   it would throw a LazyInitializationException (session already closed).
   *   @Transactional keeps the session open for the entire method execution.
   *
   * @param username The username extracted from the login request body.
   * @return UserDetails — the Spring Security representation of the user.
   * @throws UsernameNotFoundException if no user with that username exists.
   *   Spring Security catches this and returns 401 Unauthorized to the client.
   */
  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // Query the database for the user by username.
    // findByUsername() is a Spring Data JPA derived query — no SQL needed.
    // It returns Optional<User> to handle the case where the user is not found.
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

    // Convert our User JPA entity into UserDetailsImpl (Spring Security format).
    // This also converts the Set<Role> into List<GrantedAuthority>.
    return UserDetailsImpl.build(user);
  }
}
