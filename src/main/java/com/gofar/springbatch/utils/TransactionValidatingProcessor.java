package com.gofar.springbatch.utils;

import com.gofar.springbatch.entity.Customer;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.batch.item.validator.ValidationException;

/**
 * Class used to validate items before processing
 */
public class TransactionValidatingProcessor extends ValidatingItemProcessor<Customer> {

    /**
     * Class constructor
     * @param limit the limit number of transaction
     */
    public TransactionValidatingProcessor(int limit) {
        super(item -> {
            if (item.getTransactions() > limit) {
                throw new ValidationException("Customer has more than " + limit + " transactions");
            }
        });
        setFilter(true);
    }
}
