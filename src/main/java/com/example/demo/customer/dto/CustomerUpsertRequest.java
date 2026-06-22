package com.example.demo.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerUpsertRequest(
        @NotBlank
        @Size(max = 100)
        String name,

        @NotBlank
        @Email
        @Size(max = 200)
        String email,

        @Size(max = 30)
        String phoneNumber
) {
}
