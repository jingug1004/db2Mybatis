package com.example.demo.customer.mapper;

import com.example.demo.customer.domain.Customer;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMapper {

    List<Customer> findAll();

    Customer findById(Long id);

    int insert(Customer customer);

    int update(Customer customer);

    int deleteById(Long id);
}
