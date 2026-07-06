package com.banking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for signup response.
 * Contains only a success message after successful registration.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupResponse {

    private Long customerId;

    private String message;
}
