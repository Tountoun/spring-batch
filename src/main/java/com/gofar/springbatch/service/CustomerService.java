package com.gofar.springbatch.service;

import com.gofar.springbatch.dto.CustomerDto;
import com.gofar.springbatch.entity.Customer;
import com.gofar.springbatch.exception.CustomerException;
import com.gofar.springbatch.mapper.CustomerMapper;
import com.gofar.springbatch.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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
        if (customerDto.getIdentifiant() != null) {
            throw new CustomerException("Customer must not have an id");
        }
        if (customerRepository.existsByCode(customerDto.getCode())) {
            LOG.error("Customer with code {} already exists", customerDto.getCode());
            throw new CustomerException("Customer with code " + customerDto.getCode() + " already exists");
        }
        Customer result = customerRepository.save(mapper.customerDtoToCustomer(customerDto));
        return mapper.customerToCustomerDto(result);
    }

    public CustomerDto findOneByCode(String code) {
        Optional<Customer> optionalCustomer = customerRepository.findByCode(code);
        return mapper.customerToCustomerDto(optionalCustomer.orElseThrow(() -> new CustomerException("Customer not found")));
    }

    public CustomerDto findOneById(Long id) {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        return mapper.customerToCustomerDto(optionalCustomer.orElseThrow(() -> new CustomerException("Customer not found")));
    }

    public List<CustomerDto> findAll() {
        return customerRepository.findAll().stream()
                .map(mapper::customerToCustomerDto).collect(Collectors.toList());
    }

    public CustomerDto update(Long id, CustomerDto customerDto) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new CustomerException("Customer not found"));
        if (!Objects.isNull(customerDto.getIdentifiant()) && id != 0 && customerRepository.existsById(customerDto.getIdentifiant())
        ) {
            LOG.error("Customer id {} already used", customerDto.getIdentifiant());
            throw new CustomerException("Customer id already used");
        }
        if (customerRepository.existsByCode(customerDto.getCode())) {
            LOG.error("Customer code {} already used", customerDto.getCode());
            throw new CustomerException("Customer code already used");
        }
        customer.update(customerDto);
        return mapper.customerToCustomerDto(customerRepository.saveAndFlush(customer));
    }

    @Autowired
    public void setCustomerRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<CustomerDto> findAll(Pageable pageable) {
        return customerRepository.findAll(pageable).stream()
                .map(mapper::customerToCustomerDto).collect(Collectors.toList());
    }
}
