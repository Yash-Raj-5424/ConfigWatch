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

        ConfigResponse response = ConfigResponse.newBuilder()
                .putValues("enabled", "true")
                .putValues("maxRetries", "3")
                .setVersion(1)
                .build();

        System.out.println("Configuration requested by: " + serviceName);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
