package com.banking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for login response.
 * Contains user details returned after successful authentication.
 * Note: The password is never included in the response.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private Long id;

    private Long customerId;

    private String fullName;

    private String email;

    private String token;

    private String message;
}
