package com.mycompany.helloworld.health;
import com.codahale.metrics.health.HealthCheck.Result;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class TestHealthCheck {

    @Test
    public void nullConfigShouldFailHealthCheck() {
        ConfigHealthCheck configHealthCheck = new ConfigHealthCheck(null);
        Result actual = configHealthCheck.check();
        assertThat(actual.isHealthy()).isEqualTo(false);
        assertThat(actual.getMessage()).isEqualTo("There is no default Message!");
    }

    @Test
    public void nonEmptyConfigShouldPassHealthCheck() {
        ConfigHealthCheck configHealthCheck = new ConfigHealthCheck("Some String");
        Result actual = configHealthCheck.check();
        assertThat(actual.isHealthy()).isEqualTo(true);
    }
}
