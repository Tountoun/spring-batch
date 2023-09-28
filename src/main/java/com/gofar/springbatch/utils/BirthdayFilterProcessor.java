package com.gofar.springbatch.utils;

import com.gofar.springbatch.entity.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDate;

/**
 * Custom processor
 */
public class BirthdayFilterProcessor implements ItemProcessor<Customer, Customer> {
    private final static Logger log =  LoggerFactory.getLogger(BirthdayFilterProcessor.class);

    /**
     * Method for filtering customers who are born in the current month
     * @param customer
     * @return
     * @throws Exception
     */
    @Override
    public Customer process(Customer customer) throws Exception {
        log.info("Processing customer with id={}, name={} {}", customer.getId(), customer.getLastName(), customer.getFirstName());
        if (customer.getBirthDay().getMonth().equals(LocalDate.now().getMonth())) {
            return customer;
        }
        return null;
    }
}
