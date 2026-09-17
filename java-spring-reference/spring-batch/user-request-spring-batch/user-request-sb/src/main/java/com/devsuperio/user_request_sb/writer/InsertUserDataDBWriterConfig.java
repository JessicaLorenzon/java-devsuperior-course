package com.devsuperio.user_request_sb.writer;

import com.devsuperio.user_request_sb.entity.User;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InsertUserDataDBWriterConfig {

    @Bean
    public ItemWriter<User> insertUserDataDBWriter() {
        return users -> users.forEach(System.out::println);
    }
}
