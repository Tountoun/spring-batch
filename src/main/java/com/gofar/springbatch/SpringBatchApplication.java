package com.gofar.springbatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import static com.gofar.springbatch.utils.StartDataProcess.prepareTestData;

@EnableBatchProcessing
@EnableScheduling
@SpringBootApplication
public class SpringBatchApplication {

    private static final Logger log = LoggerFactory.getLogger(SpringBatchApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchApplication.class, args);
        prepareTestData(10);
    }


}
