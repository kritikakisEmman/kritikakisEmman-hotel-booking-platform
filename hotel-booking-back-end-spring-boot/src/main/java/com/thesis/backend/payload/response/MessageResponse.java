package com.thesis.backend.payload.response;

/**
 * MessageResponse - Generic DTO for simple text responses.
 *
 * Used when the backend needs to return a plain message to the frontend,
 * such as success confirmations or error descriptions.
 *
 * Examples:
 *   HTTP 200: { "message": "User registered successfully!" }
 *   HTTP 400: { "message": "Error: Username is already taken!" }
 *
 * WHY a dedicated class instead of returning a plain String?
 * Returning a raw String from a @RestController would produce:
 *   User registered successfully!      ← not valid JSON
 *
 * Wrapping it in MessageResponse produces proper JSON:
 *   { "message": "User registered successfully!" }  ← valid JSON
 *
 * The Angular frontend can then consistently read response.message
 * regardless of whether it's a success or error response.
 *
 * This is a simple but important design pattern in REST APIs:
 * always return JSON, never raw strings.
 */
public class MessageResponse {

  private String message;

  /**
   * Constructor — creates a response with the given message.
   * Used in AuthController for both success and error cases:
   *   return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
   *   return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
   */
  public MessageResponse(String message) {
    this.message = message;
  }

  // Getter required by Jackson to include "message" in the JSON output.
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
