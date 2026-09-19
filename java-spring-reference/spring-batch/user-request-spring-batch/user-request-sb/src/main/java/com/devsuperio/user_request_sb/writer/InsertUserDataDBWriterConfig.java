package com.devsuperio.user_request_sb.writer;

import com.devsuperio.user_request_sb.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class InsertUserDataDBWriterConfig {

    private static Logger logger = LoggerFactory.getLogger(InsertUserDataDBWriterConfig.class);

    @Bean
    public ItemWriter<User> insertUserDataDBWriter(@Qualifier("appDB") DataSource dataSource) {

        logger.info("[WRITER STEP] Inserting user data ...");

        return new JdbcBatchItemWriterBuilder<User>()
                .dataSource(dataSource)
                .sql("INSERT INTO tb_user (login, name, avatar_url) VALUES (:login, :name, :avatarUrl)")
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }
}
