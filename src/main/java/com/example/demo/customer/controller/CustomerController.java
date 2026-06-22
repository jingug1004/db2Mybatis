package com.example.demo.customer.controller;

import com.example.demo.customer.dto.CustomerCreateRequest;
import com.example.demo.customer.dto.CustomerResponse;
import com.example.demo.customer.dto.CustomerUpdateRequest;
import com.example.demo.customer.service.CustomerService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<CustomerResponse> findAll() {
        return customerService.findAll();
    }

    @GetMapping("/{id}")
    public CustomerResponse findById(@PathVariable Long id) {
        return customerService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse create(@Valid @RequestBody CustomerCreateRequest request) {
        return customerService.create(request);
    }

    @PutMapping("/{id}")
    public CustomerResponse update(@PathVariable Long id, @Valid @RequestBody CustomerUpdateRequest request) {
        return customerService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        customerService.delete(id);
    }
}
