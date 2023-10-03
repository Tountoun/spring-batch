package com.gofar.springbatch.repository;

import com.gofar.springbatch.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByCode(String code);

    Optional<Customer> findByCode(String code);
}
