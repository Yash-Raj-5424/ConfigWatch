package com.yash.ConfigWatch;

import com.yash.config.proto.ConfigResponse;
import io.grpc.stub.StreamObserver;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigSubscriptionManager {

    private final ConcurrentHashMap<String, Set<StreamObserver<ConfigResponse>>> subscribers =
            new ConcurrentHashMap<>();

    public void subscribe(
            String serviceName,
            StreamObserver<ConfigResponse> observer
    ){
        subscribers.computeIfAbsent(
                serviceName,
                key -> ConcurrentHashMap.newKeySet()
        ).add(observer);
    }

    public void unsubscribe(
            String serviceName,
            StreamObserver<ConfigResponse> observer
    ){

        Set<StreamObserver<ConfigResponse>> serviceSubscribers =
                subscribers.get(serviceName);

        if(serviceSubscribers != null){
            serviceSubscribers.remove(observer);

            if(serviceSubscribers.isEmpty())
                subscribers.remove(serviceName, serviceSubscribers);
        }
    }

    public void publish(String serviceName, ConfigResponse config){

        Set<StreamObserver<ConfigResponse>> serviceSubscribers =
                subscribers.get(serviceName);

        if(serviceSubscribers == null)    return;

        for(StreamObserver<ConfigResponse> observer: serviceSubscribers){

            try{
                observer.onNext(config);
            }catch (RuntimeException e){
                unsubscribe(serviceName, observer);
            }
        }

    }
}
