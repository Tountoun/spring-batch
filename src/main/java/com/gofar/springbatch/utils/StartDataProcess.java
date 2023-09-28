package com.gofar.springbatch.utils;

import com.gofar.springbatch.entity.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.beans.Encoder;
import java.beans.Expression;
import java.beans.PersistenceDelegate;
import java.beans.XMLEncoder;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

@Slf4j
@Component
public class StartDataProcess {

    public static String XML_FILe;

    public static void prepareTestData(final int amount) {
        final Collection<Customer> customers = new LinkedList<>();
        for (int i = 1; i <= amount; i++) {

            final Customer customer = new Customer();
            customer.setId((long) i);
            customer.setFirstName(UUID.randomUUID().toString().replaceAll("[^a-z]", ""));
            customer.setLastName(UUID.randomUUID().toString().replaceAll("[^a-z]", ""));
            customer.setBirthDay(LocalDate.now());
            customer.setTransactions(random());
            customers.add(customer);
        }
        try (final XMLEncoder encoder = new XMLEncoder(new FileOutputStream(XML_FILe))) {
            encoder.setPersistenceDelegate(LocalDate.class, new PersistenceDelegate() {
                @Override
                protected Expression instantiate(Object oldInstance, Encoder out) {
                    LocalDate localDate = (LocalDate) oldInstance;
                    return new Expression(oldInstance, LocalDate.class, "parse", new Object[]{localDate.toString()});
                }
            });
            encoder.writeObject(customers);
        } catch (final FileNotFoundException e) {
            log.error(e.getMessage(), e);
            System.exit(-1);
        }
    }

    private static int random() {
        return (int) Math.round(Math.random() * (100));
    }

    @Value("${gofar.batch.xml_input_file}")
    public void setFilename(String name) {
        XML_FILe = name;
    }
}
