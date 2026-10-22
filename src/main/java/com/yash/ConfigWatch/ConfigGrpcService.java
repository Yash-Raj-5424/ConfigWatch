package com.yash.ConfigWatch;

import com.yash.config.proto.ConfigRequest;
import com.yash.config.proto.ConfigResponse;
import com.yash.config.proto.ConfigServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

public class ConfigGrpcService extends ConfigServiceGrpc.ConfigServiceImplBase{

    @Override
    public void getConfig(
            ConfigRequest request,
            StreamObserver<ConfigResponse> responseObserver
    ){

        String serviceName = request.getServiceName();

        System.out.println("Configuration requested by: " + serviceName);

        ConfigResponse.Builder response = ConfigResponse.newBuilder()
                .setVersion(1);

        switch (serviceName){
            case "order-service" -> response
                    .putValues("order.enabled", "true")
                    .putValues("order.maxItems", "10");

            case "payment-service" -> response
                    .putValues("payment-enabled", "true")
                    .putValues("payment.maxRetries", "3");

            default -> {
                responseObserver.onError(
                        io.grpc.Status.NOT_FOUND
                                .withDescription("Unknown service: " + serviceName)
                                .asRuntimeException()
                );
                return;
            }
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void watchConfig(
            ConfigRequest request,
            StreamObserver<ConfigResponse> responseObserver
    ){

        String serviceName = request.getServiceName();

        if(!serviceName.equals("order-service")
            && !serviceName.equals("payment-service")
        ){
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Unknown service: " + serviceName)
                            .asRuntimeException()
            );
            return;
        }

        try{
            for(int i = 1; i <= 2; i++){
                ConfigResponse response = ConfigResponse.newBuilder()
                        .putValues("enabled", "true")
                        .putValues("maxRetries", String.valueOf(i+2))
                        .setVersion(i)
                        .build();

                responseObserver.onNext(response);
                TimeUnit.SECONDS.sleep(3);
            }
            responseObserver.onCompleted();
        } catch(InterruptedException e){
            Thread.currentThread().interrupt();
            responseObserver.onError(
                    Status.CANCELLED
                            .withDescription("Stream interrupted")
                            .withCause(e)
                            .asRuntimeException()
            );
        }
    }
}
