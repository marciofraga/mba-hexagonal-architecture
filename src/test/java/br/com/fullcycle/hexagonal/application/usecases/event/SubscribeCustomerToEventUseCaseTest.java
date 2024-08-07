package br.com.fullcycle.hexagonal.application.usecases.event;

import br.com.fullcycle.hexagonal.application.repository.InMemoryCustomerRepository;
import br.com.fullcycle.hexagonal.application.repository.InMemoryEventRepository;
import br.com.fullcycle.hexagonal.application.repository.InMemoryTicketRepository;
import br.com.fullcycle.hexagonal.application.domain.customer.Customer;
import br.com.fullcycle.hexagonal.application.domain.customer.CustomerId;
import br.com.fullcycle.hexagonal.application.domain.event.Event;
import br.com.fullcycle.hexagonal.application.domain.event.EventId;
import br.com.fullcycle.hexagonal.application.domain.partner.Partner;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.application.domain.event.ticket.TicketStatus;
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
        final var ticketRepository = new InMemoryTicketRepository();
        
        customerRepository.create(anCustomer);
        eventRepository.create(anEvent);
        
        final var useCase = new SubscribeCustomerToEventUseCase(customerRepository, eventRepository, ticketRepository);
        final var output = useCase.execute(subscribeInput);

        assertEquals(eventID, output.eventId());
        assertNotNull(output.ticketId());
        assertNotNull(output.reservationDate());
        assertEquals(TicketStatus.PENDING.name(), output.ticketStatus());
        
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
        final var ticketRepository = new InMemoryTicketRepository();

        customerRepository.create(anCustomer);

        final var useCase = new SubscribeCustomerToEventUseCase(customerRepository, eventRepository, ticketRepository);
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
        final var ticketRepository = new InMemoryTicketRepository();

        eventRepository.create(anEvent);

        final var useCase = new SubscribeCustomerToEventUseCase(customerRepository, eventRepository, ticketRepository);
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
        final var ticketRepository = new InMemoryTicketRepository();

        final var ticket = anEvent.reserveTicket((anCustomer.getCustomerId()));
        
        customerRepository.create(anCustomer);
        eventRepository.create(anEvent);
        ticketRepository.create(ticket);

        final var useCase = new SubscribeCustomerToEventUseCase(customerRepository, eventRepository, ticketRepository);
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
        final var ticketRepository = new InMemoryTicketRepository();

        final var ticket = anEvent.reserveTicket(anCustomer2.getCustomerId());
        
        customerRepository.create(anCustomer);
        customerRepository.create(anCustomer2);
        eventRepository.create(anEvent);
        ticketRepository.create(ticket);

        final var useCase = new SubscribeCustomerToEventUseCase(customerRepository, eventRepository, ticketRepository);
        final var exception = assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        assertEquals(expectedError, exception.getMessage());
    }

}