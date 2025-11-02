package com.taskflow.userservice.dto;

import lombok.*;

/*
 * DTO used by authenticated users to update their basic profile information.
 * - email: optional new email address
 * - password: optional new password (sent in plain text; it will be hashed)
 *
 * Security note: Do not log the password. The password should be hashed
 * server-side before being stored (this is done in UserService).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {
    private String email;
    private String password;
}

