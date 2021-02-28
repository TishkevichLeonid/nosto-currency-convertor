package com.nosto.currency.convertor.exception.handler.template;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class ExceptionArgumentsResponseTemplate {

    @JsonProperty("message")
    private String message;

    @JsonProperty("status")
    private int status;

    @JsonProperty("exception")
    private String exception;

    @JsonProperty("error_arguments")
    private Map<String, Object> errorArguments;
}