package com.whiskels.notifier;

import org.testcontainers.containers.PostgreSQLContainer;

public class SharedPostgresContainer extends PostgreSQLContainer<SharedPostgresContainer> {
    private static final String IMAGE_VERSION = "postgres:11.1";
    private static SharedPostgresContainer container;

    private SharedPostgresContainer() {
        super(IMAGE_VERSION);
    }

    public static SharedPostgresContainer getInstance() {
        if (container == null) {
            container = new SharedPostgresContainer();
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
