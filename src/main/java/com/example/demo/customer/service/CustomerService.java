package com.example.demo.customer.service;

import com.example.demo.customer.domain.Customer;
import com.example.demo.customer.dto.AffectedRowsResponse;
import com.example.demo.customer.dto.CustomerCopyRequest;
import com.example.demo.customer.dto.CustomerCreateRequest;
import com.example.demo.customer.dto.CustomerResponse;
import com.example.demo.customer.dto.CustomerStagingBulkRequest;
import com.example.demo.customer.dto.CustomerUpdateRequest;
import com.example.demo.customer.dto.CustomerUpsertRequest;
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

    public List<CustomerResponse> search(String name, String email) {
        return customerMapper.search(name, email)
                .stream()
                .map(CustomerResponse::from)
                .toList();
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
    public CustomerResponse upsertByEmail(CustomerUpsertRequest request) {
        Customer customer = new Customer();
        customer.setName(request.name());
        customer.setEmail(request.email());
        customer.setPhoneNumber(request.phoneNumber());

        customerMapper.mergeByEmail(customer);
        return CustomerResponse.from(getCustomerByEmail(request.email()));
    }

    @Transactional
    public CustomerResponse copyFromCustomer(Long sourceCustomerId, CustomerCopyRequest request) {
        int insertedRows = customerMapper.insertCopyFromCustomer(sourceCustomerId, request.newEmail());
        if (insertedRows == 0) {
            throw new CustomerNotFoundException(sourceCustomerId);
        }

        return CustomerResponse.from(getCustomerByEmail(request.newEmail()));
    }

    @Transactional
    public AffectedRowsResponse stageCustomers(CustomerStagingBulkRequest request, String status) {
        int insertedRows = customerMapper.insertStagingBatch(request.customers(), status);
        return new AffectedRowsResponse(insertedRows);
    }

    @Transactional
    public AffectedRowsResponse importFromStaging(String status) {
        int insertedRows = customerMapper.insertFromStaging(status);
        return new AffectedRowsResponse(insertedRows);
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

    private Customer getCustomerByEmail(String email) {
        Customer customer = customerMapper.findByEmail(email);
        if (customer == null) {
            throw new CustomerNotFoundException("email", email);
        }
        return customer;
    }
}
