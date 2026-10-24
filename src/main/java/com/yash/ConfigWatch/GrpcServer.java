package com.yash.ConfigWatch;

import com.yash.config.proto.ConfigResponse;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GrpcServer {

    static void updateConfig(
            ConfigStore store,
            ConfigSubscriptionManager manager,
            String serviceName,
            String key,
            String value
    ){

        ConfigResponse updated = store.updateConfig(serviceName, key, value);

        manager.publish(serviceName, updated);
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        ConfigStore configStore = new ConfigStore();
        ConfigSubscriptionManager subscriptionManager = new ConfigSubscriptionManager();

        Server server = ServerBuilder.forPort(9090)
                .addService(new ConfigGrpcService(configStore, subscriptionManager))
                .build()
                .start();

        new Thread(() -> {

            try{
                Thread.sleep(30_000);

                updateConfig(
                        configStore,
                        subscriptionManager,
                "order-service",
                        "order.maxItems",
                        "20"
                );

                System.out.println("Configuration updated");
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }).start();

        System.out.println("gRPC server started on port 9090");

        Runtime.getRuntime().addShutdownHook(
                new Thread( () -> {
                    System.out.println("Shutting down gRPC server..");
                    server.shutdown();
                })
        );

        server.awaitTermination();
    }
}
