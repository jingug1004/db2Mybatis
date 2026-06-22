package com.example.demo.customer.service;

import com.example.demo.customer.domain.Customer;
import com.example.demo.customer.dto.CustomerCreateRequest;
import com.example.demo.customer.dto.CustomerResponse;
import com.example.demo.customer.dto.CustomerUpdateRequest;
import com.example.demo.customer.exception.CustomerNotFoundException;
import com.example.demo.customer.mapper.CustomerMapper;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerMapper customerMapper;

    public CustomerService(CustomerMapper customerMapper) {
        this.customerMapper = customerMapper;
    }

    public List<CustomerResponse> findAll() {
        return customerMapper.findAll()
                .stream()
                .map(CustomerResponse::from)
                .toList();
    }

    public CustomerResponse findById(Long id) {
        return CustomerResponse.from(getCustomer(id));
    }

    @Transactional
    public CustomerResponse create(CustomerCreateRequest request) {
        Customer customer = new Customer();
        customer.setName(request.name());
        customer.setEmail(request.email());
        customer.setPhoneNumber(request.phoneNumber());

        customerMapper.insert(customer);
        return CustomerResponse.from(getCustomer(customer.getId()));
    }

    @Transactional
    public CustomerResponse update(Long id, CustomerUpdateRequest request) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setName(request.name());
        customer.setEmail(request.email());
        customer.setPhoneNumber(request.phoneNumber());

        int updatedRows = customerMapper.update(customer);
        if (updatedRows == 0) {
            throw new CustomerNotFoundException(id);
        }

        return CustomerResponse.from(getCustomer(id));
    }

    @Transactional
    public void delete(Long id) {
        int deletedRows = customerMapper.deleteById(id);
        if (deletedRows == 0) {
            throw new CustomerNotFoundException(id);
        }
    }

    private Customer getCustomer(Long id) {
        Customer customer = customerMapper.findById(id);
        if (customer == null) {
            throw new CustomerNotFoundException(id);
        }
        return customer;
    }
}
