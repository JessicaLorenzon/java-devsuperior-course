package com.devsuperior.hello_world_spring_batch.config;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchConfig {

    @Bean
    public Job job(JobRepository jobRepository, Step printHelloStep) {
        return new JobBuilder("job", jobRepository)
                .start(printHelloStep)
                .incrementer(new RunIdIncrementer())
                .build();
    }
}
