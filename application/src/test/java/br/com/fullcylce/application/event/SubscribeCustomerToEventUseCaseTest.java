package br.com.fullcylce.application.event;

import br.com.fullcycle.application.event.SubscribeCustomerToEventUseCase;
import br.com.fullcycle.domain.customer.Customer;
import br.com.fullcycle.domain.customer.CustomerId;
import br.com.fullcycle.domain.event.Event;
import br.com.fullcycle.domain.event.EventId;
import br.com.fullcycle.domain.event.ticket.TicketStatus;
import br.com.fullcycle.domain.exceptions.ValidationException;
import br.com.fullcycle.domain.partner.Partner;
import br.com.fullcylce.application.repository.InMemoryCustomerRepository;
import br.com.fullcylce.application.repository.InMemoryEventRepository;
import br.com.fullcylce.application.repository.InMemoryTicketRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SubscribeCustomerToEventUseCaseTest {


    @Test
    @DisplayName("Deve comprar um ticket de um evento")
    void testReserveTicket() {

        final var expectedTicketSize = 1;
        
        final var aPartner = Partner
                .create("John Doe", "12.345.678/0001-00", "john.doe@gmail.com");

        final var anEvent = Event.create("Disney on ice", "2021-01-01", 10, aPartner);
        final var anCustomer = Customer.create("Chico Doe", "123.456.789-01", "chico.doe@gmail.com");
        
        final var customerID = anCustomer.getCustomerId().value();
        final var eventID = anEvent.getEventId().value();
        

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(eventID, customerID);

        final var customerRepository = new InMemoryCustomerRepository();
        final var eventRepository = new InMemoryEventRepository();
        
        customerRepository.create(anCustomer);
        eventRepository.create(anEvent);
        
        final var useCase = new SubscribeCustomerToEventUseCase(customerRepository, eventRepository);
        final var output = useCase.execute(subscribeInput);

        assertEquals(eventID, output.eventId());
        assertNotNull(output.reservationDate());
        
        final var actualEvent = eventRepository.eventOfId(anEvent.getEventId());
        assertEquals(expectedTicketSize, actualEvent.get().getTickets().size());
    }

    @Test
    @DisplayName("Não deve comprar um ticket de um evento que não existe")
    void testReserveTicketWithoutEvent() {

        final var expectedError = "Event not found";
        
        final var anCustomer = Customer.create("Chico Doe", "123.456.789-01", "chico.doe@gmail.com");

        final var customerID = anCustomer.getCustomerId();
        final var eventID = EventId.unique();


        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(eventID.value(), customerID.value());

        final var customerRepository = new InMemoryCustomerRepository();
        final var eventRepository = new InMemoryEventRepository();

        customerRepository.create(anCustomer);

        final var useCase = new SubscribeCustomerToEventUseCase(customerRepository, eventRepository);
        final var exception = assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        assertEquals(expectedError, exception.getMessage());
    }

    @Test
    @DisplayName("Não deve comprar um ticket de um cliente não existente")
    void testReserveTicketWithoutCustomer() {

        final var expectedError = "Customer not found";
        final var aPartner = Partner
                .create("John Doe", "12.345.678/0001-00", "john.doe@gmail.com");

        final var anEvent = Event.create("Disney on ice", "2021-01-01", 10, aPartner);

        final var customerID = CustomerId.unique();
        final var eventID = anEvent.getEventId();


        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(eventID.value(), customerID.value());

        final var customerRepository = new InMemoryCustomerRepository();
        final var eventRepository = new InMemoryEventRepository();

        eventRepository.create(anEvent);

        final var useCase = new SubscribeCustomerToEventUseCase(customerRepository, eventRepository);
        final var exception = assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        assertEquals(expectedError, exception.getMessage());
    }

    @Test
    @DisplayName("Um mesmo cliente não pode comprar dois tickets para o mesmo evento")
    void testReserveTicketMoreThanOnce() {

        final var expectedError = "Email already registered";
        final var aPartner = Partner
                .create("John Doe", "12.345.678/0001-00", "john.doe@gmail.com");

        final var anEvent = Event.create("Disney on ice", "2021-01-01", 10, aPartner);
        final var anCustomer = Customer.create("Chico Doe", "123.456.789-01", "chico.doe@gmail.com");

        final var customerID = anCustomer.getCustomerId();
        final var eventID = anEvent.getEventId();


        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(eventID.value(), customerID.value());

        final var customerRepository = new InMemoryCustomerRepository();
        final var eventRepository = new InMemoryEventRepository();

        anEvent.reserveTicket((anCustomer.getCustomerId()));
        
        customerRepository.create(anCustomer);
        eventRepository.create(anEvent);

        final var useCase = new SubscribeCustomerToEventUseCase(customerRepository, eventRepository);
        final var exception = assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        assertEquals(expectedError, exception.getMessage());
    }

    @Test
    @DisplayName("Um cliente não deve comprar um ticket de um evento esgotado")
    void testReserveTicketWithoutSlots() {

        final var expectedError = "Event sold out";
        final var aPartner = Partner
                .create("John Doe", "12.345.678/0001-00", "john.doe@gmail.com");

        final var anEvent = Event.create("Disney on ice", "2021-01-01", 1, aPartner);
        final var anCustomer = Customer.create("Chico Doe", "123.456.789-01", "chico.doe@gmail.com");
        final var anCustomer2 = Customer.create("Francisco Doe", "124.456.789-01", "chico.doe@gmail.com");

        final var customerID = anCustomer.getCustomerId();
        final var eventID = anEvent.getEventId();


        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(eventID.value(), customerID.value());

        final var customerRepository = new InMemoryCustomerRepository();
        final var eventRepository = new InMemoryEventRepository();

        anEvent.reserveTicket(anCustomer2.getCustomerId());
        
        customerRepository.create(anCustomer);
        customerRepository.create(anCustomer2);
        eventRepository.create(anEvent);

        final var useCase = new SubscribeCustomerToEventUseCase(customerRepository, eventRepository);
        final var exception = assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        assertEquals(expectedError, exception.getMessage());
    }

}