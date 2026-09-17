package com.devsuperio.user_request_sb.writer;

import com.devsuperio.user_request_sb.dto.UserDTO;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InsertUserDataDBWriterConfig {

    @Bean
    public ItemWriter<UserDTO> insertUserDataDBWriter() {
        return users -> users.forEach(System.out::println);
    }
}
