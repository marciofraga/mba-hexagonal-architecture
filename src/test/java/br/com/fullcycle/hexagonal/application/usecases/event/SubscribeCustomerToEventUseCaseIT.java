package br.com.fullcycle.hexagonal.application.usecases.event;

import br.com.fullcycle.hexagonal.AbstractIntegrationTest;
import br.com.fullcycle.hexagonal.application.domain.customer.Customer;
import br.com.fullcycle.hexagonal.application.domain.customer.CustomerId;
import br.com.fullcycle.hexagonal.application.domain.event.Event;
import br.com.fullcycle.hexagonal.application.domain.event.EventId;
import br.com.fullcycle.hexagonal.application.domain.event.ticket.Ticket;
import br.com.fullcycle.hexagonal.application.domain.event.ticket.TicketStatus;
import br.com.fullcycle.hexagonal.application.domain.partner.Partner;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.application.repositories.CustomerRepository;
import br.com.fullcycle.hexagonal.application.repositories.EventRepository;
import br.com.fullcycle.hexagonal.application.repositories.TicketRepository;
import io.hypersistence.tsid.TSID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SubscribeCustomerToEventUseCaseIT extends AbstractIntegrationTest {

    @Autowired
    private SubscribeCustomerToEventUseCase useCase;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private TicketRepository ticketRepository;
    
    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();
        customerRepository.deleteAll();
        ticketRepository.deleteAll();
    }
    
    @Test
    @DisplayName("Deve comprar um ticket de um evento")
    void testReserveTicket() {

        final var aCustomer = createCustomer("123.456.789-00", "john.doe@gmail.com", "John Doe");
        final var aPartner = Partner.create("John Doe", "12.345.678/0001-00", "john.doe@gmail.com");
        final var aEvent = createEvent("Disney on Ice", "2025-01-01", 10, aPartner);

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(aEvent.getEventId().value(), aCustomer.getCustomerId().value());
        final var output = useCase.execute(subscribeInput);

        assertEquals(aEvent.getEventId().value(), output.eventId());
        assertNotNull(output.reservationDate());
        assertEquals(TicketStatus.PENDING.name(), output.ticketStatus());
    }

    @Test
    @DisplayName("Não deve comprar um ticket de um evento que não existe")
    void testReserveTicketWithoutEvent() {

        final var expectedError = "Event not found";
        final var customer = createCustomer("123.456.789-00", "john.doe@gmail.com", "John Doe");
        final var eventID = EventId.unique();

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(eventID.value(), customer.getCustomerId().value());
        final var exception = assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        assertEquals(expectedError, exception.getMessage());
    }

    @Test
    @DisplayName("Não deve comprar um ticket de um cliente não existente")
    void testReserveTicketWithoutCustomer() {

        final var expectedError = "Customer not found";
        final var customerID = CustomerId.unique().value();
        final var eventID = EventId.unique().value();

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(eventID, customerID);
        final var exception = assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        assertEquals(expectedError, exception.getMessage());
    }

    @Test
    @DisplayName("Um cliente não deve comprar um ticket de um evento esgotado")
    void testReserveTicketWithoutSlots() {

        final var expectedError = "Event sold out";
        final var aCustomer = createCustomer("123.456.789-00", "john.doe@gmail.com", "John Doe");
        final var aPartner = Partner.create("John Doe", "12.345.678/0001-00", "john.doe@gmail.com");
        final var aEvent = createEvent("Event test", "2025-01-01", 0, aPartner);

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(aEvent.getEventId().value(), aCustomer.getCustomerId().value());
        final var exception = assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        assertEquals(expectedError, exception.getMessage());
    }

    private Customer createCustomer(final String cpf, final String email, final String name) {
        return customerRepository.create(Customer.create(name, cpf, email));
    }

    private Event createEvent(final String name, final String date, final int totalSpots, Partner partner) {
        return eventRepository.create(Event.create(name, date, totalSpots, partner));
    }

    private void createTicket(final Ticket ticket) {
        ticketRepository.create(ticket);
    }
}