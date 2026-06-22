package com.example.demo.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerCopyRequest(
        @NotBlank
        @Email
        @Size(max = 200)
        String newEmail
) {
}
