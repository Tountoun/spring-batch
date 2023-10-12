package com.gofar.springbatch.config;

import com.gofar.springbatch.entity.Customer;
import com.gofar.springbatch.repository.CustomerRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobFactory;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DBDataToFileJobConfig {
    private static final String DB_DATA_TO_CSV_FILE_STEP = "databaseToCsvStep";
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Value("${gofar.batch.csv.output}")
    private String csvFileName;

    @Bean
    public Job backupJob() {
        return jobBuilderFactory.get("loadFileDataJob")
                .incrementer(new RunIdIncrementer())
                .start(databaseToCsvStep())
                .build();
    }

    @Bean
    public Step databaseToCsvStep() {
        return stepBuilderFactory.get(DB_DATA_TO_CSV_FILE_STEP)
                .<Customer, Customer>chunk(3)
                .reader(repositoryItemReader())
                .processor(backupProcessor())
                .writer(csvItemWriter())
                .build();
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

    @StepScope
    @Bean
    public ItemProcessor<Customer, Customer> backupProcessor() {
        return (customer) -> {
            if (customer.getTransactions() != 0) {
                return customer;
            }
            return null;
        };
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

    private DelimitedLineAggregator<Customer> getLineAggregator() {
        BeanWrapperFieldExtractor<Customer> extractor = new BeanWrapperFieldExtractor<>();
        extractor.setNames(new String[]{"id", "code", "firstName", "lastName", "birthDay", "lastUpdate", "creationDate", "transactions"});

        DelimitedLineAggregator<Customer> aggregator = new DelimitedLineAggregator<>();
        aggregator.setDelimiter(",");
        aggregator.setFieldExtractor(extractor);
        return aggregator;
    }
}
