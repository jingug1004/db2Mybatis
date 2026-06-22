package com.example.demo.customer.exception;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(Long id) {
        super("Customer not found. id=" + id);
    }

    public CustomerNotFoundException(String field, String value) {
        super("Customer not found. " + field + "=" + value);
    }
}
