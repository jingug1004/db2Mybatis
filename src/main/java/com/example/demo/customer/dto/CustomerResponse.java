package com.example.demo.customer.dto;

import com.example.demo.customer.domain.Customer;
import java.time.LocalDateTime;

public record CustomerResponse(
        Long id,
        String name,
        String email,
        String phoneNumber,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }
}
