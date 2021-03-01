package com.nosto.currency.convertor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nosto.currency.convertor.CurrencyConvertorApplication;
import com.nosto.currency.convertor.controller.dto.CurrencyConvertorResponseDTO;
import com.nosto.currency.convertor.controller.dto.ExchangeRateResponseDTO;
import com.nosto.currency.convertor.rest.ExchangeRatesRestClient;
import com.nosto.currency.convertor.util.ResponsesUtil;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CurrencyConvertorApplication.class})
public class CurrencyConvertorControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private ExchangeRatesRestClient exchangeRatesRestClient;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        ExchangeRateResponseDTO exchangeRateResponseDTO = ResponsesUtil.validExchangeEURtoUSDServiceResponse();
        Mockito.when(exchangeRatesRestClient.getExchangeRates(ArgumentMatchers.anyString()))
                .thenReturn(exchangeRateResponseDTO);
    }

    @Test
    public void successConvertationTest() throws Exception {
        ExchangeRateResponseDTO exchangeRateResponseDTO = ResponsesUtil.validExchangeEURtoUSDServiceResponse();
        String url = "/convert?source=EUR&target=USD&amount=1000";
        MvcResult mvcResult = mockMvc.perform(get(url)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        CurrencyConvertorResponseDTO currencyConvertorResponseDTO =
                objectMapper.readValue(body, CurrencyConvertorResponseDTO.class);
        BigDecimal convertationAmount = exchangeRateResponseDTO.getRates().get("USD")
                .multiply(BigDecimal.valueOf(1000)).stripTrailingZeros();

        Assertions.assertThat(currencyConvertorResponseDTO.getAmount()).isEqualTo(convertationAmount);
        Assertions.assertThat(currencyConvertorResponseDTO.getSource()).isEqualTo("EUR");
        Assertions.assertThat(currencyConvertorResponseDTO.getTarget()).isEqualTo("USD");
    }

    @Test
    public void missingSourceRequestParams() throws Exception {
        String urlSourceIsMissing = "/convert?target=EUR&amount=1000";
        String sourceIsMissingError = "Required String parameter 'source' is not present";
        testErrorRequestParam(urlSourceIsMissing, sourceIsMissingError);
    }

    @Test
    public void missingTargetRequestParams() throws Exception {
        String urlTargetIsMissing = "/convert?source=EUR&amount=1000";
        String targetIsMissingError = "Required String parameter 'target' is not present";
        testErrorRequestParam(urlTargetIsMissing, targetIsMissingError);
    }

    @Test
    public void missingAmountRequestParams() throws Exception {
        String urlAmountIsMissing = "/convert?source=EUR&target=USD";
        String amountIsMissingError = "Required BigDecimal parameter 'amount' is not present";
        testErrorRequestParam(urlAmountIsMissing, amountIsMissingError);
    }

    @Test
    public void sourceAndTargetAreEqual() throws Exception {
        String url = "/convert?source=EUR&target=EUR&amount=1000";
        mockMvc.perform(get(url)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void sourceAsInvalidRequestParam() throws Exception {
        String urlSourceIsShorter = "/convert?source=EU&target=USD&amount=1000";
        String sourceIsMissingError = "convert.source: Short currency name must be 3 characters long";
        testErrorRequestParam(urlSourceIsShorter, sourceIsMissingError);
        String urlSourceIsLonger = "/convert?source=EURR&target=USD&amount=1000";
        testErrorRequestParam(urlSourceIsLonger, sourceIsMissingError);
    }

    @Test
    public void targetAsInvalidRequestParam() throws Exception {
        String urlTargetIsShorter = "/convert?source=EUR&target=US&amount=1000";
        String sourceIsMissingError = "convert.target: Short currency name must be 3 characters long";
        testErrorRequestParam(urlTargetIsShorter, sourceIsMissingError);
        String urlTargetIsLonger = "/convert?source=EUR&target=USDD&amount=1000";
        testErrorRequestParam(urlTargetIsLonger, sourceIsMissingError);
    }

    @Test
    public void amountAsInvalidRequestParam() throws Exception {
        String urlAmountIsZero = "/convert?source=EUR&target=USD&amount=0";
        String sourceIsMissingError = "convert.amount: Amount should be greater then 0";
        testErrorRequestParam(urlAmountIsZero, sourceIsMissingError);
        String urlAmountIsNegative = "/convert?source=EUR&target=USD&amount=-3";
        testErrorRequestParam(urlAmountIsNegative, sourceIsMissingError);
    }

    private void testErrorRequestParam(String url, String errorMessage) throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(url)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String missingString = mvcResult.getResponse().getContentAsString();
        Assertions.assertThat(missingString.contains(errorMessage))
                .isEqualTo(true);
    }
}
