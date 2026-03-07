package com.thesis.backend.models;

import javax.persistence.*;

/**
 * Role - JPA Entity that represents the "roles" table in the database.
 *
 * DATABASE TABLE: "roles"
 * +----+----------------+
 * | id | name           |
 * +----+----------------+
 * |  1 | ROLE_ADMIN     |
 * |  2 | ROLE_MODERATOR |
 * |  3 | ROLE_USER      |
 * +----+----------------+
 *
 * WHY a separate table for roles instead of a column in "users"?
 * Because a user can have MULTIPLE roles (e.g. admin AND moderator).
 * This requires a Many-to-Many relationship between users and roles,
 * which is handled in User.java with a join table "user_roles".
 *
 * SPRING / JPA ANNOTATIONS EXPLAINED:
 *
 * @Entity
 *   Tells Hibernate: "This Java class maps to a database table."
 *   Without this, Hibernate completely ignores the class.
 *   Hibernate will look for a table with the same name as the class ("role")
 *   unless you specify a different name with @Table.
 *
 * @Table(name = "roles")
 *   Overrides the default table name. Without this, Hibernate would look
 *   for a table named "role" (lowercase class name). We want "roles".
 */
@Entity
@Table(name = "roles")
public class Role {

  /**
   * @Id
   *   Marks this field as the PRIMARY KEY of the table.
   *   Every JPA entity MUST have exactly one @Id field.
   *
   * @GeneratedValue(strategy = GenerationType.IDENTITY)
   *   Tells Hibernate to let the DATABASE generate the ID automatically.
   *   GenerationType.IDENTITY uses MySQL's AUTO_INCREMENT.
   *   So you never set the id manually — the database assigns it.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * @Enumerated(EnumType.STRING)
   *   Tells Hibernate HOW to store the enum value in the database.
   *   EnumType.STRING → stores the name as text: "ROLE_ADMIN", "ROLE_USER"
   *   EnumType.ORDINAL (default) → stores the position number: 0, 1, 2
   *   We use STRING because if you reorder the enum values,
   *   ORDINAL would break all existing data.
   *
   * @Column(length = 20)
   *   Sets the max length of the VARCHAR column in MySQL.
   *   "ROLE_MODERATOR" has 14 characters, so 20 is enough.
   */
  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private ERole name;

  /**
   * Default no-argument constructor.
   * JPA REQUIRES a no-arg constructor in every entity.
   * Hibernate uses it to create instances via reflection when
   * loading data from the database.
   */
  public Role() {
  }

  /**
   * Convenience constructor to create a Role with a name.
   * Used in AuthController when assigning roles to a new user.
   */
  public Role(ERole name) {
    this.name = name;
  }

  // Standard getters and setters.
  // JPA accesses fields through these methods (field access or property access).
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public ERole getName() {
    return name;
  }

  public void setName(ERole name) {
    this.name = name;
  }
}
