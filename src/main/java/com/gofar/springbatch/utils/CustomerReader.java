package com.gofar.springbatch.utils;

import com.gofar.springbatch.entity.Customer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.IteratorItemReader;

import java.beans.XMLDecoder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class CustomerReader implements ItemReader<Customer> {

    private ItemReader<Customer> delegate;
    public CustomerReader() {
        // Do something
    }

    @Override
    public Customer read() throws Exception {
        if (delegate == null) {
            delegate = new IteratorItemReader<>(customers());
        }
        return delegate.read();
    }

    private List<Customer> customers() {
        try {
            XMLDecoder xmlDecoder = new XMLDecoder(new FileInputStream(StartDataProcess.XML_FILe));
            return (List<Customer>) xmlDecoder.readObject();
        } catch (FileNotFoundException e) {
            return null;
        }
    }
}
