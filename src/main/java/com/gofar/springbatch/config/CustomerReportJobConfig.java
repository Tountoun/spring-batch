package com.gofar.springbatch.config;

import com.gofar.springbatch.entity.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Class for job configuration
 */
@Configuration
@Slf4j
public class CustomerReportJobConfig {

    private static final String CHUNK_STEP = "chunk-step";

    private static final String JOB_NAME = "customerReportJob";

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobLauncher jobLauncher;

    @Scheduled(cron = "${gofar.customer.batch.cron-expression}")
    public void run() throws Exception {
        JobParameters jobParameters =  new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        JobExecution execution = jobLauncher.run(
                reportJob(),
                jobParameters
        );
        log.info("Job: name = {} id = {}",execution.getJobInstance().getJobName(), execution.getId());
    }

    @Bean
    public Job reportJob() {
        return jobBuilderFactory.get(JOB_NAME)
                .start(chunkStep())
                .build();
    }

    /**
     * Step based on chunk oriented-processing
     * @return a step
     */
    @Bean
    public Step chunkStep() {
        return stepBuilderFactory.get(CHUNK_STEP)
                .<Customer, Customer>chunk(20)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    @StepScope
    @Bean
    public ItemReader<Customer> itemReader() {
        return new CustomerReader();
    }

    @StepScope
    @Bean
    public ItemProcessor<Customer, Customer> itemProcessor() {
        return (customer) -> {
            if (customer.getTransactions() == 0) {
                throw new ValidationException("Customer has no transaction");
            }
            return customer;
        };
    }

    @StepScope
    @Bean
    public ItemWriter<Customer> itemWriter() {
        return new CustomerWriter();
    }
}
