package com.ria.acmetesting.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration
public class DBContainers extends PostgreSQLContainer<DBContainers> {
    private static final String IMAGE_VERSION = "postgres:13.10";
    private static DBContainers container;

    private DBContainers() {
        super(IMAGE_VERSION);
    }

    public static DBContainers getInstance() {
        if (container == null) {
            container = new DBContainers();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("DB_URL", container.getJdbcUrl());
        System.setProperty("DB_USERNAME", container.getUsername());
        System.setProperty("DB_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }
}
