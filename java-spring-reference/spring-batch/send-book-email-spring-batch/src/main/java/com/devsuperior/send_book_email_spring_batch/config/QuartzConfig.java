package com.devsuperior.send_book_email_spring_batch.config;

import com.devsuperior.send_book_email_spring_batch.job.SendBookLoanNotificationScheduleJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail quartzJobDetail() {
        return JobBuilder.newJob(SendBookLoanNotificationScheduleJob.class).storeDurably().build();
    }

    @Bean
    public Trigger jobTrigger() {
        String exp = "0 51 18 * * ?";
        return TriggerBuilder
                .newTrigger()
                .forJob(quartzJobDetail())
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule(exp))
                .build();
    }
}
