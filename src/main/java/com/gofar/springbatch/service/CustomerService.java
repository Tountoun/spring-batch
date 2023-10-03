package com.gofar.springbatch.service;

import com.gofar.springbatch.dto.CustomerDto;
import com.gofar.springbatch.entity.Customer;
import com.gofar.springbatch.exception.CustomerException;
import com.gofar.springbatch.mapper.CustomerMapper;
import com.gofar.springbatch.repository.CustomerRepository;
import com.gofar.springbatch.utils.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    private CustomerRepository customerRepository;
    private static final Logger LOG = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerMapper mapper;

    @Autowired
    public CustomerService(CustomerMapper mapper) {
        this.mapper = mapper;
    }

    public CustomerDto save(CustomerDto customerDto) {
        if (customerRepository.existsByCode(customerDto.getCode())) {
            LOG.error("Customer with code {} not found", customerDto.getCode());
            throw new CustomerException("Customer with code " + customerDto.getCode() + " not found");
        }
        Customer result = customerRepository.save(mapper.customerDtoToCustomer(customerDto));
        return mapper.customerToCustomerDto(result);
    }

    public CustomerDto findOne(String code) {
        Optional<Customer> optionalCustomer = customerRepository.findByCode(code);
        return mapper.customerToCustomerDto(optionalCustomer.orElseThrow(() -> new CustomerException("Customer not found")));
    }

    public List<CustomerDto> findAll() {
        return customerRepository.findAll().stream()
                .map(mapper::customerToCustomerDto).collect(Collectors.toList());
    }

    @Autowired
    public void setCustomerRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
}
