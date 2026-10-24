package com.yash.ConfigWatch;

import com.yash.config.proto.ConfigRequest;
import com.yash.config.proto.ConfigResponse;
import com.yash.config.proto.ConfigServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

public class ConfigGrpcService extends ConfigServiceGrpc.ConfigServiceImplBase{

    private final ConfigStore configStore;
    private final ConfigSubscriptionManager subscriptionManager;

    public ConfigGrpcService(ConfigStore configStore, ConfigSubscriptionManager subscriptionManager) {
        this.configStore = configStore;
        this.subscriptionManager = subscriptionManager;
    }


    @Override
    public void getConfig(
            ConfigRequest request,
            StreamObserver<ConfigResponse> responseObserver
    ){

        String serviceName = request.getServiceName();
        ConfigResponse config = configStore.getConfig(serviceName);

        System.out.println("Configuration requested by: " + serviceName);

        if(config == null){
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Unknown service: " + serviceName)
                            .asRuntimeException()
            );
            return;
        }

        responseObserver.onNext(config);
        responseObserver.onCompleted();
    }

    @Override
    public void watchConfig(
            ConfigRequest request,
            StreamObserver<ConfigResponse> responseObserver
    ){

        String serviceName = request.getServiceName();
        ConfigResponse config = configStore.getConfig(serviceName);

        if (config == null) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Unknown service: " + serviceName)
                            .asRuntimeException()
            );
            return;
        }

        subscriptionManager.subscribe(serviceName, responseObserver);
        System.out.println("initial config");

        responseObserver.onNext(config); // send right now

        System.out.println("Subscribed: " + serviceName);
    }
}
