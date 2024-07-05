package br.com.fullcycle.infrastructure.gateway;

import br.com.fullcycle.application.ticket.CreateTicketForCustomerUseCase;
import br.com.fullcycle.domain.event.EventTicketReserved;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ConsumerQueueGateway implements QueueGateway{
    
    private final CreateTicketForCustomerUseCase createTicketForCustomerUseCase;
    private final ObjectMapper objectMapper;

    public ConsumerQueueGateway(CreateTicketForCustomerUseCase createTicketForCustomerUseCase, ObjectMapper objectMapper) {
        this.createTicketForCustomerUseCase = createTicketForCustomerUseCase;
        this.objectMapper = objectMapper;
    }

    @Async(value = "queueExecutor")
    @Override
    public void publish(final String content) {
        if(content == null) {
            return;
        }
        
        if(content.contains("event-ticket.reserved")) {
            final var dto = safeRead(content, EventTicketReserved.class);
            this.createTicketForCustomerUseCase.execute(new CreateTicketForCustomerUseCase.Input(
                    dto.eventTicketId(), 
                    dto.eventId(), 
                    dto.customerId()
            ));
        }
    }
    
    private <T> T safeRead(final String content, final Class<T> tClass) {
        try {
            return this.objectMapper.readValue(content, tClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
