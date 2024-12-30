package com.mycompany.helloworld.health;

import com.codahale.metrics.health.HealthCheck;

public class ConfigHealthCheck extends HealthCheck {
    private final String defaultMessage;

    public ConfigHealthCheck(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    @Override
    protected Result check() {
        if (this.defaultMessage == null) {
            return Result.unhealthy("There is no default Message!");
        }
        return Result.healthy();
    }
}