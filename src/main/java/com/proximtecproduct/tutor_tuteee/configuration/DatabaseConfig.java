package com.proximtecproduct.tutor_tuteee.configuration;

import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Bean
    public DataSource dataSource() {
        // Configure dotenv
        Dotenv dotenv = Dotenv.load();

        // Configure DataSource
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(dotenv.get("DB_URL"));
        dataSource.setUsername(dotenv.get("DB_USER"));
        dataSource.setPassword(dotenv.get("DB_PASSWORD"));
        return dataSource;
    }
}
