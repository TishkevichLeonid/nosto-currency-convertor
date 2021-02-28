package com.nosto.currency.convertor.cache;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;
import com.nosto.currency.convertor.controller.dto.ExchangeRateResponseDTO;

import java.io.InputStream;
import java.io.OutputStream;

public class ExchangeRateSerializer implements StreamSerializer<ExchangeRateResponseDTO> {

    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(ExchangeRateResponseDTO.class);
        return kryo;
    });

    @Override
    public void write(ObjectDataOutput out, ExchangeRateResponseDTO exchangeRateResponseDTO) {
        Kryo kryo = kryoThreadLocal.get();
        Output output = new Output((OutputStream) out);
        kryo.writeObject(output, exchangeRateResponseDTO);
        output.flush();
    }

    @Override
    public ExchangeRateResponseDTO read(ObjectDataInput in) {
        InputStream inputStream = (InputStream) in;
        Input input = new Input(inputStream);
        Kryo kryo = kryoThreadLocal.get();
        return kryo.readObject(input, ExchangeRateResponseDTO.class);
    }

    @Override
    public int getTypeId() {
        return 2;
    }
}
