package com.gofar.springbatch.controller;

import com.gofar.springbatch.dto.CustomerDto;
import com.gofar.springbatch.exception.CustomerException;
import com.gofar.springbatch.service.CustomerService;
import com.gofar.springbatch.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/{code}")
    public ResponseEntity<Response> getCustomer(@PathVariable("code") String code) {
        try {
            response = new Response(customerService.findOne(code), "Customer retrieved successfully", HttpStatus.OK);
        } catch (CustomerException e) {
            return ResponseEntity.badRequest().body(
                    new Response(null, e.getMessage(), HttpStatus.BAD_REQUEST)
            );
        }
        return ResponseEntity.ok().body(response);
    }

    @GetMapping
    public ResponseEntity<Response> getAllCustomers() {
        return ResponseEntity.ok().body(new Response(
                customerService.findAll(), "Customer retrieved successfully", HttpStatus.OK
        ));
    }

    @Autowired
    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }
}
