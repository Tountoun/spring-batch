package com.gofar.springbatch.config;

import com.gofar.springbatch.entity.Customer;
import com.gofar.springbatch.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Class for job configuration
 */
@Configuration
public class CustomerReportJobConfig {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerReportJobConfig.class);
    private static final String CHUNK_STEP = "chunk-step";

    private static final String JOB_NAME = "customerReportJob";

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private CustomerRepository customerRepository;

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
        LOG.info("Job: name = {} id = {}", execution.getJobInstance().getJobName(), execution.getId());
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
                .writer(repositoryItemWriter())
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

    @Bean
    public RepositoryItemWriter<Customer> repositoryItemWriter() {
        RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
        writer.setRepository(customerRepository);
        writer.setMethodName("save");
        return writer;
    }
}
