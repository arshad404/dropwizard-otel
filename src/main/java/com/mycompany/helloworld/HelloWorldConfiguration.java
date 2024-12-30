package com.mycompany.helloworld;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;

public class HelloWorldConfiguration extends Configuration {
    @NotEmpty
    private String defaultMessage;

    @JsonProperty
    public String getDefaultMessage() {
        return defaultMessage;
    }
}
