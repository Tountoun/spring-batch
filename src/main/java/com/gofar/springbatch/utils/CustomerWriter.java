package com.gofar.springbatch.utils;

import com.gofar.springbatch.entity.Customer;
import org.springframework.batch.item.ItemWriter;

import javax.annotation.PreDestroy;
import java.io.*;
import java.util.List;


public class CustomerWriter implements ItemWriter<Customer>, Closeable {

    private final PrintWriter writer;

    public CustomerWriter() {
        OutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream("output.txt");
        } catch (FileNotFoundException e) {
            outputStream = System.out;
        }
        this.writer = new PrintWriter(outputStream);
    }

    @Override
    public void write(List<? extends Customer> list) throws Exception {
        list.forEach(customer -> {
            writer.println(customer.toString());
        });
    }

    @PreDestroy
    @Override
    public void close() throws IOException {
        writer.close();
    }
}
