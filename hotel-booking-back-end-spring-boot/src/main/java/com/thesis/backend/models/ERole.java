package com.thesis.backend.models;

/**
 * ERole - Enumeration of all available user roles in the application.
 *
 * In Java, an "enum" (enumeration) is a special type that holds a fixed set
 * of constants. It is like a list of allowed values — you cannot add new ones
 * at runtime.
 *
 * WHY use an enum instead of plain Strings like "ROLE_ADMIN"?
 * - Type safety: the compiler will catch typos at compile time.
 *   If you write ERole.ROLE_ADNIN → compile error. If you use a String
 *   "ROLE_ADNIN" → silent bug at runtime.

 *
 * HOW this maps to the database:
 * The "Role" entity (Role.java) has a field of type ERole.
 * Hibernate stores it as a String ("ROLE_ADMIN", "ROLE_MODERATOR", "ROLE_USER")
 * in the "roles" table, because of the @Enumerated(EnumType.STRING) annotation.
 *
 * The three roles in this application:
 * - ROLE_ADMIN     : Platform administrator. Can manage all users and hotels.
 * - ROLE_MODERATOR : Hotel owner. Can manage their own hotel, rooms, reservations.
 * - ROLE_USER      : Customer. Can search hotels and make reservations.
 */
public enum ERole {
  ROLE_USER,
  ROLE_MODERATOR,
  ROLE_ADMIN
}
