package com.nosto.currency.convertor.cache;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.nosto.currency.convertor.controller.dto.ExchangeRateResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CacheClient {

    private static final String CURRENCY_MAP = "Currency";

    @Value("${currency-convertor-service.cache.cache-refresh-time}")
    private int cacheRefreshTime;
    @Value("${currency-convertor-service.cache.max-cache-idl-time}")
    private int maxCacheIdlTime;

    private final HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(createConfig());

    public ExchangeRateResponseDTO put(String sourceCurrency, ExchangeRateResponseDTO exchangeRateResponseDTO) {
        IMap<String, ExchangeRateResponseDTO> map = hazelcastInstance.getMap(CURRENCY_MAP);
        return map.putIfAbsent(sourceCurrency, exchangeRateResponseDTO);
    }

    public ExchangeRateResponseDTO get(String sourceCurrency) {
        IMap<String, ExchangeRateResponseDTO> map = hazelcastInstance.getMap(CURRENCY_MAP);
        return map.get(sourceCurrency);
    }

    private Config createConfig() {
        MapConfig mapConfig = new MapConfig(CURRENCY_MAP);
        mapConfig.setTimeToLiveSeconds(cacheRefreshTime);
        mapConfig.setMaxIdleSeconds(maxCacheIdlTime);
        Config config = new Config();
        config.addMapConfig(mapConfig);
        config.getSerializationConfig().addSerializerConfig(new SerializerConfig().
                setTypeClass(ExchangeRateResponseDTO.class).
                setImplementation(new ExchangeRateSerializer()));
        return config;
    }
}
