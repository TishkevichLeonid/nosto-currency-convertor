package com.nosto.currency.convertor.controller;

import com.nosto.currency.convertor.controller.dto.CurrencyConvertorResponseDTO;
import com.nosto.currency.convertor.exception.CurrencyConvertorServiceException;
import com.nosto.currency.convertor.service.CurrencyConvertorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@RestController
@Slf4j
@Validated
public class CurrencyConvertorController {

    private static final String CURRENCY_ERROR_MESSAGE = "Short currency name must be 3 characters long";
    private static final String AMOUNT_ERROR_MESSAGE = "Amount should be greater then 0";

    private final CurrencyConvertorService currencyConvertorService;

    @Autowired
    public CurrencyConvertorController(CurrencyConvertorService currencyConvertorService) {
        this.currencyConvertorService = currencyConvertorService;
    }

    @GetMapping("/convert")
    public CurrencyConvertorResponseDTO convert(@RequestParam(value = "source")
                                                @Size(max = 3, min = 3, message = CURRENCY_ERROR_MESSAGE) String source,
                                                @RequestParam(value = "target")
                                                @Size(max = 3, min = 3, message = CURRENCY_ERROR_MESSAGE) String target,
                                                @RequestParam(value = "amount")
                                                @Positive(message = AMOUNT_ERROR_MESSAGE) BigDecimal amount) {
        log.info("Received a request for currency conversion, with parameters: source: {}, target: {}, amount: {}",
                source, target, amount);
        if (source.equals(target)) {
            log.warn("Source and target currencies are equals. Source = {}, Target = {}", source, target);
            throw new CurrencyConvertorServiceException("Please, specify different currencies to convert");
        }
        return currencyConvertorService.convert(source, target, amount);
    }
}
