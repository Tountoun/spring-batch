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
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.HashMap;
import java.util.Map;

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

    @Value("${gofar.batch.csv.output}")
    private String csvFileName;

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
                .incrementer(new RunIdIncrementer())
                .start(chunkStep())
                .next(databaseToCsvStep())
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

    @Bean
    public Step databaseToCsvStep() {
        return stepBuilderFactory.get("databaseToCsvStep")
                .<Customer, Customer>chunk(20)
                .reader(repositoryItemReader())
                .processor(csvItemProcessor())
                .writer(csvItemWriter())
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
            if (customer.getTransactions() != 0 && !customerRepository.existsByCode(customer.getCode())) {
                return customer;
            }
            return null;
        };
    }
    @StepScope
    @Bean
    public ItemProcessor<Customer, Customer> csvItemProcessor() {
        return (customer) -> {
            if (customer.getTransactions() != 0) {
                return customer;
            }
            return null;
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

    @Bean
    public FlatFileItemWriter<Customer> csvItemWriter() {
        FlatFileItemWriter<Customer> itemWriter = new FlatFileItemWriter<>();
        itemWriter.setResource(new FileSystemResource(csvFileName));
        itemWriter.setEncoding("UTF-8");
        itemWriter.setLineAggregator(getLineAggregator());
        itemWriter.setHeaderCallback(headerCallback());
        return itemWriter;
    }

    private FlatFileHeaderCallback headerCallback() {
        return writer -> {
          writer.write("id,code,firstname,lastname,birthday,last-update,creation-date,transactions");
        };
    }

    @Bean
    public RepositoryItemReader<Customer> repositoryItemReader() {
        Map<String, Sort.Direction> sorts = new HashMap<>();
        sorts.put("id", Sort.Direction.ASC);
        return new RepositoryItemReaderBuilder<Customer>()
                .name("customer-repository-reader")
                .repository(customerRepository)
                .methodName("findAll")
                .pageSize(10)
                .sorts(sorts)
                .build();
    }


    private DelimitedLineAggregator<Customer> getLineAggregator() {
        BeanWrapperFieldExtractor<Customer> extractor = new BeanWrapperFieldExtractor<>();
        extractor.setNames(new String[]{"id", "code", "firstName", "lastName", "birthDay", "lastUpdate", "creationDate", "transactions"});

        DelimitedLineAggregator<Customer> aggregator = new DelimitedLineAggregator<>();
        aggregator.setDelimiter(",");
        aggregator.setFieldExtractor(extractor);
        return aggregator;
    }
}
