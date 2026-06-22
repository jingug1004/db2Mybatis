package com.example.demo.customer.mapper;

import com.example.demo.customer.domain.Customer;
import com.example.demo.customer.dto.CustomerStagingRequest;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CustomerMapper {

    List<Customer> findAll();

    Customer findById(@Param("id") Long id);

    Customer findByEmail(@Param("email") String email);

    List<Customer> search(@Param("name") String name, @Param("email") String email);

    int insert(Customer customer);

    int mergeByEmail(Customer customer);

    int insertCopyFromCustomer(@Param("sourceCustomerId") Long sourceCustomerId, @Param("newEmail") String newEmail);

    int insertStagingBatch(@Param("customers") List<CustomerStagingRequest> customers, @Param("status") String status);

    int insertFromStaging(@Param("status") String status);

    int update(Customer customer);

    int deleteById(@Param("id") Long id);
}
