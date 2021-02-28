package com.nosto.currency.convertor.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyConvertorResponseDTO {

    @JsonProperty("conversion_result")
    private BigDecimal amount;

    @JsonProperty("source_currency")
    private String source;

    @JsonProperty("target_currency")
    private String target;
}
