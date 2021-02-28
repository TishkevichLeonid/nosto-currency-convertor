package com.nosto.currency.convertor.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
public class ExchangeRateResponseDTO {

    @JsonProperty(value = "base")
    private String base;

    @JsonProperty(value = "date")
    private LocalDate date;

    @JsonProperty(value = "rates")
    private Map<String, BigDecimal> rates;
}
