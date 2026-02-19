package com.wyh.aijobsearchassistant.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

/**
 * Data Source Configuration
 * Configures SQLite data source for local storage
 * Adapted for Java 21 modularization
 */
@Configuration
public class DataSourceConfig {

    /**
     * Configure SQLite DataSource
     * Uses environment variable DB_PATH or default path
     */
    @Bean
    public DataSource dataSource() {
        String dbPath = System.getenv().getOrDefault("DB_PATH", "./data/job_assistant.db");

        // Ensure data directory exists
        java.io.File dataDir = new java.io.File(dbPath).getParentFile();
        if (dataDir != null && !dataDir.exists()) {
            dataDir.mkdirs();
        }

        return DataSourceBuilder.create()
            .driverClassName("org.sqlite.JDBC")
            .url("jdbc:sqlite:" + dbPath)
            .build();
    }
}
