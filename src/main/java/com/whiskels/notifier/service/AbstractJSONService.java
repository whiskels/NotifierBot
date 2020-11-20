package com.whiskels.notifier.service;

import org.springframework.beans.factory.annotation.Value;

public abstract class AbstractJSONService {
    @Value("${heroku.server.hour.offset}")
    protected int serverHourOffset;

    abstract void update();
}
