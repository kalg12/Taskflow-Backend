package com.taskflow.userservice.dto;

import lombok.*;

/*
 * Lombok annotations used on this class:
 * - @Getter / @Setter: generate getters and setters at compile time.
 * - @NoArgsConstructor / @AllArgsConstructor: generate a no-arg constructor and an all-args constructor.
 * - @Builder: generate a fluent builder API (provides a static builder() method).
 *
 * Purpose: Reduce boilerplate — repetitive code such as getters, setters,
 * constructors, equals/hashCode, and toString that do not contain business logic.
 *
 * Description: This DTO represents an authentication response carrying a token
 * (for example a JWT). The `token` field should contain the authentication token
 * returned to the client after a successful login.
 *
 * Requirements:
 * - Add Lombok dependency to your `pom.xml` (groupId: org.projectlombok, artifactId: lombok).
 * - Enable "Annotation Processing" in IntelliJ so the IDE/compiler recognize generated code.
 *
 * Note: Lombok generates code at compile time; the generated methods are not visible
 * in source files but are available to the compiler and runtime when annotation
 * processing is enabled.
 */

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthResponse {
    private String token;
}
