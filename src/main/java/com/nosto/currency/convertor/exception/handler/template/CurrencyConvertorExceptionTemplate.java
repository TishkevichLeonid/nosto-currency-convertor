package com.nosto.currency.convertor.exception.handler.template;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
public class CurrencyConvertorExceptionTemplate {

    @JsonProperty("dateTime")
    private OffsetDateTime dateTime;

    @JsonProperty("status")
    private HttpStatus status;

    @JsonProperty("message")
    private String message;
}
