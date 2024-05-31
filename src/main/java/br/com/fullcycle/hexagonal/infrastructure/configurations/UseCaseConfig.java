package br.com.fullcycle.hexagonal.infrastructure.configurations;

import br.com.fullcycle.hexagonal.application.usecases.CreateCustomerUseCase;
import br.com.fullcycle.hexagonal.application.usecases.CreateEventUseCase;
import br.com.fullcycle.hexagonal.application.usecases.CreatePartnerUseCase;
import br.com.fullcycle.hexagonal.application.usecases.GetCustomerByIdUseCase;
import br.com.fullcycle.hexagonal.application.usecases.GetPartnerByIdUseCase;
import br.com.fullcycle.hexagonal.application.usecases.SubscribeCustomerToEventUseCase;
import br.com.fullcycle.hexagonal.infrastructure.services.CustomerService;
import br.com.fullcycle.hexagonal.infrastructure.services.EventService;
import br.com.fullcycle.hexagonal.infrastructure.services.PartnerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class UseCaseConfig {

    private final CustomerService customerService;
    private final EventService eventService;
    private final PartnerService partnerService;


    public UseCaseConfig(CustomerService customerService, EventService eventService, PartnerService partnerService) {
        this.customerService = Objects.requireNonNull(customerService);
        this.eventService = Objects.requireNonNull(eventService);
        this.partnerService = Objects.requireNonNull(partnerService);
    }

    @Bean
    public CreateCustomerUseCase createCustomerUseCase() {
        return new CreateCustomerUseCase(customerService);
    }

    @Bean
    public CreateEventUseCase createEventUseCase() {
        return new CreateEventUseCase(eventService, partnerService);
    }

    @Bean
    public CreatePartnerUseCase createPartnerUseCase() {
        return new CreatePartnerUseCase(partnerService);
    }

    @Bean
    public GetPartnerByIdUseCase getPartnerByIdUseCase() {
        return new GetPartnerByIdUseCase(partnerService);
    }

    @Bean
    public GetCustomerByIdUseCase getCustomerByIdUseCase() {
        return new GetCustomerByIdUseCase(customerService);
    }

    @Bean
    public SubscribeCustomerToEventUseCase subscribeCustomerToEventUseCase() {
        return new SubscribeCustomerToEventUseCase(customerService, eventService);
    }
}