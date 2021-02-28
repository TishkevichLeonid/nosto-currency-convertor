package com.nosto.currency.convertor.service;

import com.nosto.currency.convertor.cache.CacheClient;
import com.nosto.currency.convertor.controller.dto.CurrencyConvertorResponseDTO;
import com.nosto.currency.convertor.controller.dto.ExchangeRateResponseDTO;
import com.nosto.currency.convertor.exception.CurrencyNotSupportedException;
import com.nosto.currency.convertor.rest.ExchangeRatesRestClient;
import com.nosto.currency.convertor.util.ResponsesUtil;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

@RunWith(MockitoJUnitRunner.class)
public class CurrencyConvertorServiceTest {

    @Mock
    private ExchangeRatesRestClient exchangeRatesRestClient;

    @Mock
    private CacheClient cacheClient;

    @InjectMocks
    private CurrencyConvertorService currencyConvertorService;

    @Test
    public void successConvertationViaCache() {
        ExchangeRateResponseDTO exchangeRateResponseDTO = ResponsesUtil.validExchangeEURtoUSDServiceResponse();
        Mockito.when(cacheClient.get(ArgumentMatchers.anyString()))
                .thenReturn(exchangeRateResponseDTO);
        BigDecimal convertationAmount = exchangeRateResponseDTO.getRates().get("USD").multiply(BigDecimal.valueOf(1000));
        CurrencyConvertorResponseDTO convertorResponseDTO =
                currencyConvertorService.convert("EUR", "USD", BigDecimal.valueOf(1000));

        Mockito.verify(exchangeRatesRestClient,
                Mockito.times(0)).getExchangeRates(ArgumentMatchers.anyString());
        Assertions.assertThat(convertorResponseDTO.getAmount()).isEqualTo(convertationAmount);
        Assertions.assertThat(convertorResponseDTO.getSource()).isEqualTo("EUR");
        Assertions.assertThat(convertorResponseDTO.getTarget()).isEqualTo("USD");
    }

    @Test
    public void successConvertationViaRest() {
        ExchangeRateResponseDTO exchangeRateResponseDTO = ResponsesUtil.validExchangeEURtoUSDServiceResponse();
        Mockito.when(exchangeRatesRestClient.getExchangeRates(ArgumentMatchers.anyString()))
                .thenReturn(exchangeRateResponseDTO);
        BigDecimal convertationAmount = exchangeRateResponseDTO.getRates().get("USD").multiply(BigDecimal.valueOf(1000));
        CurrencyConvertorResponseDTO convertorResponseDTO =
                currencyConvertorService.convert("EUR", "USD", BigDecimal.valueOf(1000));

        Assertions.assertThat(convertorResponseDTO.getAmount()).isEqualTo(convertationAmount);
        Assertions.assertThat(convertorResponseDTO.getSource()).isEqualTo("EUR");
        Assertions.assertThat(convertorResponseDTO.getTarget()).isEqualTo("USD");
    }

    @Test(expected = CurrencyNotSupportedException.class)
    public void invalidTarget() {
        Mockito.when(cacheClient.get(ArgumentMatchers.anyString()))
                .thenReturn(ResponsesUtil.validExchangeEURtoUSDServiceResponse());
        currencyConvertorService.convert("EUR", "Invalid", BigDecimal.valueOf(1000));
    }
}
