package com.nosto.currency.convertor.util;

import com.nosto.currency.convertor.controller.dto.ExchangeRateResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;

public class ResponsesUtil {

    public static ExchangeRateResponseDTO validExchangeEURtoUSDServiceResponse() {
        Map<String, BigDecimal> rates = Collections.singletonMap("USD", BigDecimal.valueOf(1.21));
        ExchangeRateResponseDTO responseDTO = new ExchangeRateResponseDTO();
        responseDTO.setRates(rates);
        responseDTO.setBase("EUR");
        responseDTO.setDate(LocalDate.now());
        return responseDTO;
    }
}
