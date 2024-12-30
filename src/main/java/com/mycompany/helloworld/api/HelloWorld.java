package com.mycompany.helloworld.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HelloWorld {
    private String content;

    @JsonCreator
    public HelloWorld(@JsonProperty("content") String content) {
        this.content = content;
    }

    @JsonProperty("content")
    public String getContent(){
        return this.content;
    }
}