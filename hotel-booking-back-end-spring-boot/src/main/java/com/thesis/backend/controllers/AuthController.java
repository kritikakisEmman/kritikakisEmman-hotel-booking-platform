package com.thesis.backend.controllers;

import com.thesis.backend.models.ERole;
import com.thesis.backend.models.Role;
import com.thesis.backend.models.User;
import com.thesis.backend.payload.request.LoginRequest;
import com.thesis.backend.payload.request.SignupRequest;
import com.thesis.backend.payload.response.JwtResponse;
import com.thesis.backend.payload.response.MessageResponse;
import com.thesis.backend.repository.RoleRepository;
import com.thesis.backend.repository.UserRepository;
import com.thesis.backend.security.jwt.JwtUtils;
import com.thesis.backend.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * AuthController - Handles user registration and login (authentication endpoints).
 *
 * This is the ONLY controller that is completely public (no JWT required).
 * It provides two endpoints:
 *   POST /api/auth/signin  → Login: validates credentials, returns JWT token
 *   POST /api/auth/signup  → Register: creates a new user account
 *
 * @CrossOrigin(origins = "*", maxAge = 3600)
 *   Allows requests from ANY origin (domain) to this controller.
 *   origins = "*" means all domains are allowed.
 *   maxAge = 3600 means the browser can cache the CORS preflight response for 1 hour.
 *   In production, you'd restrict this to your frontend domain.
 *
 * @RestController
 *   Combination of @Controller + @ResponseBody.
 *   @Controller marks this as a Spring MVC controller (handles HTTP requests).
 *   @ResponseBody tells Spring to serialize return values directly to JSON
 *   (via Jackson), instead of looking for a view/template to render.
 *
 * @RequestMapping("/api/auth")
 *   All endpoints in this controller are prefixed with "/api/auth".
 *   So @PostMapping("/signin") becomes POST /api/auth/signin.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  /**
   * @Autowired dependencies — Spring injects all of these automatically.
   *
   * AuthenticationManager:
   *   The core Spring Security component that processes authentication.
   *   We call authenticate() on it during login to verify credentials.
   *   It internally calls UserDetailsServiceImpl.loadUserByUsername()
   *   and BCryptPasswordEncoder.matches() to verify the password.
   *
   * UserRepository / RoleRepository:
   *   Spring Data JPA repositories for database operations on User and Role tables.
   *
   * PasswordEncoder:
   *   BCryptPasswordEncoder bean defined in WebSecurityConfig.
   *   Used to hash passwords before saving them to the database.
   *
   * JwtUtils:
   *   Our utility class that generates JWT tokens after successful login.
   */
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  /**
   * signin - Authenticates a user and returns a JWT token.
   *
   * HTTP: POST /api/auth/signin
   * Body: { "username": "admin", "password": "myPassword" }
   *
   * @PostMapping("/signin")
   *   Maps HTTP POST requests to "/api/auth/signin" to this method.
   *
   * @Validated
   *   Triggers Bean Validation on the LoginRequest object.
   *   If username or password is blank (@NotBlank), Spring returns 400 Bad Request
   *   before this method even runs.
   *
   * @RequestBody LoginRequest loginRequest
   *   Spring reads the JSON body of the request and deserializes it
   *   into a LoginRequest object (using Jackson).
   *
   * ResponseEntity<?>
   *   A Spring wrapper around the HTTP response that lets us control:
   *   - The response body (the JWT or error message)
   *   - The HTTP status code (200 OK, 400 Bad Request, etc.)
   *   The <?> wildcard means it can return different body types.
   */
  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Validated @RequestBody LoginRequest loginRequest) {

    // Step 1: Ask Spring Security to authenticate the user.
    // UsernamePasswordAuthenticationToken is a simple container holding
    // the credentials (username + password) before verification.
    // authenticationManager.authenticate() internally:
    //   1. Calls userDetailsService.loadUserByUsername(loginRequest.getUsername())
    //   2. Calls passwordEncoder.matches(loginRequest.getPassword(), storedHash)
    //   3. If both succeed, returns an Authentication object with user details + authorities
    //   4. If credentials are wrong, throws BadCredentialsException → 401
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    // Step 2: Store the authentication in the SecurityContext for this request.
    // This is not strictly necessary for the login endpoint, but it's good practice
    // and allows access to the current user later in the same request.
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // Step 3: Generate the JWT token from the authenticated user's data.
    // The token contains the username and expiration time, signed with our secret key.
    String jwt = jwtUtils.generateJwtToken(authentication);

    // Step 4: Extract user details from the Authentication object.
    // getPrincipal() returns the UserDetailsImpl object we created in loadUserByUsername().
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    // Step 5: Extract the list of role names as strings.
    // getAuthorities() returns Collection<GrantedAuthority> (e.g. [ROLE_ADMIN, ROLE_USER])
    // .stream() creates a stream to process each element
    // .map(item -> item.getAuthority()) converts each GrantedAuthority to its String name
    // .collect(Collectors.toList()) gathers results into a List<String>
    List<String> roles = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());

    // Step 6: Return the JWT token + user info as a JSON response (HTTP 200 OK).
    // ResponseEntity.ok() wraps the body with HTTP status 200.
    // The Angular frontend stores this token in localStorage and sends it
    // in the Authorization header of all subsequent requests.
    return ResponseEntity.ok(new JwtResponse(jwt,
        userDetails.getId(),
        userDetails.getUsername(),
        userDetails.getEmail(),
        roles));
  }

  /**
   * registerUser - Creates a new user account.
   *
   * HTTP: POST /api/auth/signup
   * Body: { "username": "john", "email": "john@hotels.com", "password": "pass123", "role": ["mod"] }
   *
   * The "role" field in the request body determines what role the user gets:
   *   null or missing → ROLE_USER (customer)
   *   ["mod"]         → ROLE_MODERATOR (hotel owner)
   *   ["admin"]       → ROLE_ADMIN (platform admin)
   *   ["user"]        → ROLE_USER (explicitly)
   *
   * NOTE: In a production app, you would NOT allow clients to specify their own role.
   * The admin would assign roles. Or you'd have a separate admin endpoint for that.
   */
  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Validated @RequestBody SignupRequest signUpRequest) {

    // Validate uniqueness before creating the user.
    // These queries hit the database. If duplicates exist, return 400 Bad Request.
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Username is already taken!"));
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Email is already in use!"));
    }

    // Create new User entity.
    // encoder.encode() hashes the password with BCrypt before storing.
    // NEVER store plain text passwords in the database.
    User user = new User(signUpRequest.getUsername(),
        signUpRequest.getEmail(),
        encoder.encode(signUpRequest.getPassword()));

    // Determine which roles to assign to this user.
    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      // No role specified → default to ROLE_USER (customer).
      // orElseThrow: if ROLE_USER doesn't exist in the roles table, throw exception.
      // This would happen if the roles table was not seeded with initial data.
      Role userRole = roleRepository.findByName(ERole.ROLE_USER)
          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      // Process each role string from the request.
      // forEach() iterates over each element in the Set.
      strRoles.forEach(role -> {
        System.out.print(role);
        switch (role) {
          case "admin":
            // Find ROLE_ADMIN in the database and assign it.
            System.out.print("mpika sto admin section");
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(adminRole);
            break;
          case "mod":
            // Find ROLE_MODERATOR in the database and assign it.
            System.out.print("mpika sto mod section");
            Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(modRole);
            break;
          default: {
            // Any other value → default to ROLE_USER.
            System.out.print("mpika sto default section");
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            System.out.print("to userRole einai " + userRole.getName());
            roles.add(userRole);
          }
        }
      });
    }

    // Assign the determined roles to the user and save to database.
    // userRepository.save() performs an INSERT (since id is null — new entity).
    // Hibernate also inserts the corresponding rows in the user_roles join table.
    user.setRoles(roles);
    userRepository.save(user);

    // Return a simple success message with HTTP 200 OK.
    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }
}
