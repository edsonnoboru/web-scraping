package com.noboru.webscraping.datasource;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("postgres")
public class SourceDataSourceConfigurationLocal {
    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSource sourceDataSource() {
        return DataSourceBuilder.create().build();
    }
}
