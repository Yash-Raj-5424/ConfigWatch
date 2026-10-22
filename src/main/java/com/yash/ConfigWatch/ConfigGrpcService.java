package com.yash.ConfigWatch;

import com.yash.config.proto.ConfigRequest;
import com.yash.config.proto.ConfigResponse;
import com.yash.config.proto.ConfigServiceGrpc;
import io.grpc.stub.StreamObserver;

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
}
