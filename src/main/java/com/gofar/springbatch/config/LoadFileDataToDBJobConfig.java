package com.gofar.springbatch.config;

import com.gofar.springbatch.entity.Customer;
import com.gofar.springbatch.repository.CustomerRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class LoadFileDataToDBJobConfig {
    private static final String LOAD_XML_FILE_DATA_TO_DB_STEP = "loadXmlFileDataStep";
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private CustomerRepository customerRepository;

    @Bean
    public Job loadJob() {
        return jobBuilderFactory.get("loadFileDataJob")
                .incrementer(new RunIdIncrementer())
                .start(loadXmlFileDataToDBStep())
                .build();
    }


    /**
     * Step based on chunk oriented-processing
     * @return a step
     */
    @Bean
    public Step loadXmlFileDataToDBStep() {
        return stepBuilderFactory.get(LOAD_XML_FILE_DATA_TO_DB_STEP)
                .<Customer, Customer>chunk(10)
                .reader(itemReader())
                .processor(loadProcessor())
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
    public ItemProcessor<Customer, Customer> loadProcessor() {
        return (customer) -> {
            if (customer.getTransactions() != 0 && !customerRepository.existsByCode(customer.getCode())) {
                return customer;
            }
            return null;
        };
    }

    @Bean
    public RepositoryItemWriter<Customer> repositoryItemWriter() {
        RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
        writer.setRepository(customerRepository);
        writer.setMethodName("save");
        return writer;
    }
}
