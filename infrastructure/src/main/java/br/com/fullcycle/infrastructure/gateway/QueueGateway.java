package br.com.fullcycle.infrastructure.gateway;

public interface QueueGateway {
    void publish(String content);
}
