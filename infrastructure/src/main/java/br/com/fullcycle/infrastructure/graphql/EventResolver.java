package br.com.fullcycle.infrastructure.graphql;

import br.com.fullcycle.application.event.CreateEventUseCase;
import br.com.fullcycle.application.event.SubscribeCustomerToEventUseCase;
import br.com.fullcycle.infrastructure.dtos.NewEventDTO;
import br.com.fullcycle.infrastructure.dtos.SubscribeDTO;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

@Controller
public class EventResolver {
    
    private final CreateEventUseCase createEventUseCase;
    private final SubscribeCustomerToEventUseCase subscribeCustomerToEventUseCase;

    public EventResolver(
            CreateEventUseCase createEventUseCase, 
            SubscribeCustomerToEventUseCase subscribeCustomerToEventUseCase
    ) {
        this.createEventUseCase = createEventUseCase;
        this.subscribeCustomerToEventUseCase = subscribeCustomerToEventUseCase;
    }
    
    @MutationMapping
    public CreateEventUseCase.Output createEvent(@Argument NewEventDTO input) {
        return createEventUseCase.execute(new CreateEventUseCase.Input(input.date(), input.name(), input.partnerID().toString(), input.totalSpots()));
    }

    @Transactional
    @MutationMapping
    public SubscribeCustomerToEventUseCase.Output subscribeCustomerToEvent(@Argument SubscribeDTO input) {
        return subscribeCustomerToEventUseCase.execute(new SubscribeCustomerToEventUseCase.Input(input.customerId().toString(), input.eventId().toString()));
    }
}
