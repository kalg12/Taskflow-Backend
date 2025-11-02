package com.taskflow.userservice.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Simple Role entity used as a granted authority.
 * - name: for example "USER" or "ADMIN"; JwtAuthFilter maps it to
 *   a Spring Security authority as "ROLE_USER".
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
}
