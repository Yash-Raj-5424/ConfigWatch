package com.yash.ConfigWatch;

import com.yash.config.proto.ConfigRequest;
import com.yash.config.proto.ConfigResponse;
import com.yash.config.proto.ConfigServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class ConfigClient {

    public static void main(String[] args) {

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 9090)
                .usePlaintext()
                .build();

        try{
            ConfigServiceGrpc.ConfigServiceBlockingStub stub =
                    ConfigServiceGrpc.newBlockingStub(channel);

            ConfigRequest request = ConfigRequest.newBuilder()
                    .setServiceName("xyz-service")
                    .build();

            ConfigResponse response = stub.getConfig(request);

            System.out.println("Configuration received:");
            response.getValuesMap().forEach(
                    (key, value) -> {
                        System.out.println(key + ": " + value);
                    }
            );
        }catch (StatusRuntimeException e){
            System.out.println("grpc error: " + e.getStatus());
        }
        finally {
            channel.shutdown();
        }
    }
}
