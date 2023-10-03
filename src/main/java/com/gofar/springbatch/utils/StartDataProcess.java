package com.gofar.springbatch.utils;

import com.gofar.springbatch.entity.Customer;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Component
public class StartDataProcess {

    private static final Logger LOG = LoggerFactory.getLogger(StartDataProcess.class);
    public static String XML_FILe;

    public static void prepareTestData(final int amount) {
        final Collection<Customer> customers = new LinkedList<>();
        for (int i = 1; i <= amount; i++) {

            final Customer customer = new Customer();
            customer.setId((long) i);
            customer.setCode(RandomStringUtils.random(RandomStringUtils.randomNumeric(5, 7).length(),true, false));
            customer.setFirstName(RandomStringUtils.random(RandomStringUtils.randomNumeric(5, 10).length(),true, false));
            customer.setLastName(RandomStringUtils.random(RandomStringUtils.randomNumeric(5, 10).length(),true, false));
            customer.setBirthDay(LocalDate.now());
            customer.setTransactions(RandomStringUtils.randomNumeric(0, 30).length());
            customers.add(customer);
        }
        try (final XMLEncoder encoder = new XMLEncoder(new FileOutputStream(XML_FILe, false))) {
            encoder.setPersistenceDelegate(LocalDate.class, new PersistenceDelegate() {
                @Override
                protected Expression instantiate(Object oldInstance, Encoder out) {
                    LocalDate localDate = (LocalDate) oldInstance;
                    return new Expression(oldInstance, LocalDate.class, "parse", new Object[]{localDate.toString()});
                }
            });
            encoder.writeObject(customers);
        } catch (final FileNotFoundException e) {
            LOG.error(e.getMessage(), e);
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
