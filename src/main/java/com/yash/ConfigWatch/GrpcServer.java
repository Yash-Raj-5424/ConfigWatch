package com.yash.ConfigWatch;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GrpcServer {

    public static void main(String[] args) throws IOException, InterruptedException {

        Server server = ServerBuilder.forPort(9090)
                .addService(new ConfigGrpcService())
                .build()
                .start();

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
