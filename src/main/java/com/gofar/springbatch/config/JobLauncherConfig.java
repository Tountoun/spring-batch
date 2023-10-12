package com.gofar.springbatch.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class JobLauncherConfig {
    private static final Logger LOG = LoggerFactory.getLogger(JobLauncherConfig.class);
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private LoadFileDataToDBJobConfig loadFileDataToDBJobConfig;
    @Autowired
    private DBDataToFileJobConfig dbDataToFileJobConfig;

    @Scheduled(cron = "${gofar.customer.batch.cron-expression.load}")
    public void runDataLoad() throws Exception {
        JobParameters jobParameters =  new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        JobExecution execution = jobLauncher.run(
                loadFileDataToDBJobConfig.loadJob(),
                jobParameters
        );
        LOG.info("Job: name = {} id = {}", execution.getJobInstance().getJobName(), execution.getId());
    }
    @Scheduled(cron = "${gofar.customer.batch.cron-expression.backup}")
    public void runDBBackup() throws Exception {
        JobParameters jobParameters =  new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        JobExecution execution = jobLauncher.run(
                dbDataToFileJobConfig.backupJob(),
                jobParameters
        );
        LOG.info("Job: name = {} id = {}", execution.getJobInstance().getJobName(), execution.getId());
    }
}
