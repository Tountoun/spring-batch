package com.gofar.springbatch.config;

import com.gofar.springbatch.entity.Customer;
import com.gofar.springbatch.utils.BirthdayFilterProcessor;
import com.gofar.springbatch.utils.CustomerReader;
import com.gofar.springbatch.utils.CustomerWriter;
import com.gofar.springbatch.utils.TransactionValidatingProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PreDestroy;
import java.util.Arrays;

/**
 * Class for job configuration
 */
@Configuration
@Slf4j
public class CustomerReportJobConfig {

    private static final String TASKLET_STEP = "tasklet-step";

    private static final String CHUNK_STEP = "chunk-step";

    private static final String JOB_NAME = "customerReportJob";

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobExplorer jobs;

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


    @PreDestroy
    public void destroy() throws NoSuchJobException {
        jobs.getJobNames().forEach(name -> log.info("job name: {}", name));
        jobs.getJobInstances(JOB_NAME, 0, jobs.getJobInstanceCount(JOB_NAME)).forEach(
                jobInstance -> {
                    log.info("job instance id {}", jobInstance.getInstanceId());
                }
        );

    }
    @Bean
    public Job reportJob() {
        return jobBuilderFactory.get(JOB_NAME)
                .start(chunkStep())
                .build();
    }


    @Bean
    public BirthdayFilterProcessor birthdayFilterProcessor() {
        return new BirthdayFilterProcessor();
    }

    @Bean
    public TransactionValidatingProcessor transactionValidatingProcessor() {
        return new TransactionValidatingProcessor(5);
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
        CompositeItemProcessor<Customer, Customer> compositeItemProcessor = new CompositeItemProcessor<>();
        compositeItemProcessor.setDelegates(Arrays.asList(transactionValidatingProcessor(),
                birthdayFilterProcessor()));
        return compositeItemProcessor;
    }

    @StepScope
    @Bean
    public ItemWriter<Customer> itemWriter() {
        return new CustomerWriter();
    }
}
