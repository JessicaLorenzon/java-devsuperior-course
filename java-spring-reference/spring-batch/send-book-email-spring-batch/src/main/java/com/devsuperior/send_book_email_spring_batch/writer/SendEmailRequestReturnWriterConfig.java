package com.devsuperior.send_book_email_spring_batch.writer;

import com.devsuperior.send_book_email_spring_batch.domain.UserBookLoan;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SendEmailRequestReturnWriterConfig {

    @Bean
    public ItemWriter<UserBookLoan> sendEmailRequestReturnWriter() {
        return items -> items.forEach((System.out::println));
    }
}
