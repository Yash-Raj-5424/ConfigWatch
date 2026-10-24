package com.yash.ConfigWatch;

import com.yash.config.proto.ConfigResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigStore {

    private final Map<String, ConfigResponse> configs = new ConcurrentHashMap<>();

    public ConfigStore(){

        configs.put("order-service",
                ConfigResponse.newBuilder()
                        .putValues("order.enabled", "true")
                        .putValues("order.maxItems", "10")
                        .setVersion(1)
                        .build()
        );

        configs.put("payment-service",
                ConfigResponse.newBuilder()
                        .putValues("payment.enabled", "true")
                        .putValues("payment.maxRetries", "3")
                        .setVersion(1)
                        .build()
        );
    }

    public ConfigResponse getConfig(String serviceName){
        return configs.get(serviceName);
    }

    public ConfigResponse updateConfig(
            String serviceName,
            String key,
            String value
    ){
        return configs.compute(serviceName,
                (name, current) -> {
                    if(current == null){
                        throw new IllegalArgumentException(
                                "Unknown service: " + serviceName
                        );
                    }

                    return ConfigResponse.newBuilder()
                            .putAllValues(current.getValuesMap())
                            .putValues(key, value)
                            .setVersion(current.getVersion()+1)
                            .build();
                });
    }
}
