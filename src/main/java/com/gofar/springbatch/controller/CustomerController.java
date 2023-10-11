package com.gofar.springbatch.controller;

import com.gofar.springbatch.dto.CustomerDto;
import com.gofar.springbatch.exception.CustomerException;
import com.gofar.springbatch.service.CustomerService;
import com.gofar.springbatch.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("gofar/customers")
public class CustomerController {

    private CustomerService customerService;

    private Response response;

    public CustomerController() {
        this.response = new Response();
    }

    @PostMapping
    public ResponseEntity<Response> saveCustomer(@RequestBody CustomerDto customerDto) {
        try {
            response = new Response(customerService.save(customerDto), "Customer saved successfully", HttpStatus.OK);
        } catch (CustomerException e) {
            return ResponseEntity.badRequest().body(
                    new Response(null, e.getMessage(), HttpStatus.NOT_FOUND)
            );
        }
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<Response> getCustomerByCode(@PathVariable("code") String code) {
        try {
            response = new Response(customerService.findOneByCode(code), "Customer retrieved successfully", HttpStatus.OK);
        } catch (CustomerException e) {
            return ResponseEntity.badRequest().body(
                    new Response(null, e.getMessage(), HttpStatus.BAD_REQUEST)
            );
        }
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Response> getCustomerById(@PathVariable("id") Long id) {
        try {
            response = new Response(customerService.findOneById(id), "Customer retrieved successfully", HttpStatus.OK);
        } catch (CustomerException e) {
            return ResponseEntity.badRequest().body(
                    new Response(null, e.getMessage(), HttpStatus.BAD_REQUEST)
            );
        }
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> updateCustomer(@PathVariable("id") Long id, @RequestBody CustomerDto customerDto) {
        try {
            response = new Response(customerService.update(id, customerDto), "Customer updated successfully", HttpStatus.ACCEPTED);
        } catch (CustomerException e) {
            response = new Response(null, e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping
    public ResponseEntity<Response> getAllCustomers() {
        return ResponseEntity.ok().body(new Response(
                customerService.findAll(), "Customer retrieved successfully", HttpStatus.OK
        ));
    }

    @GetMapping("/paging")
    public ResponseEntity<Response> getAllCustomers(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                                    @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size).withSort(Sort.Direction.ASC, "code");
        return ResponseEntity.ok().body(new Response(
                customerService.findAll(pageable), "Customer retrieved successfully", HttpStatus.OK
        ));
    }

    @Autowired
    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }
}
