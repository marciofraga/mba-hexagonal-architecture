package br.com.fullcycle.hexagonal.application.usecases;

import br.com.fullcycle.hexagonal.AbstractIntegrationTest;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.infrastructure.models.Customer;
import br.com.fullcycle.hexagonal.infrastructure.models.Event;
import br.com.fullcycle.hexagonal.infrastructure.models.Ticket;
import br.com.fullcycle.hexagonal.infrastructure.models.TicketStatus;
import br.com.fullcycle.hexagonal.infrastructure.repositories.CustomerRepository;
import br.com.fullcycle.hexagonal.infrastructure.repositories.EventRepository;
import br.com.fullcycle.hexagonal.infrastructure.repositories.TicketRepository;
import br.com.fullcycle.hexagonal.infrastructure.services.CustomerService;
import br.com.fullcycle.hexagonal.infrastructure.services.EventService;
import io.hypersistence.tsid.TSID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SubscribeCustomerToEventUseCaseIT extends AbstractIntegrationTest {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private EventService eventService;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private TicketRepository ticketRepository;
    
    @BeforeEach
    void tearDown() {
        eventRepository.deleteAll();
        customerRepository.deleteAll();
        ticketRepository.deleteAll();
    }
    
    @Test
    @DisplayName("Deve comprar um ticket de um evento")
    public void testReserveTicket() {

        final var expectedTicketsSize = 1;
        final var aCustomer = createCustomer("12345678900", "john.doe@gmail.com", "John Doe");
        final var aEvent = createEvent("Disney on Ice", 10);

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(aEvent.getId(), aCustomer.getId());

        final var useCase = new SubscribeCustomerToEventUseCase(customerService, eventService);
        final var output = useCase.execute(subscribeInput);

        assertEquals(aEvent.getId(), output.eventId());
        assertNotNull(output.reservationDate());
        assertEquals(TicketStatus.PENDING.name(), output.ticketStatus());
    }

    @Test
    @DisplayName("Não deve comprar um ticket de um evento que não existe")
    public void testReserveTicketWithoutEvent() {

        final var expectedError = "Event not found";
        final var customer = createCustomer("12345678900", "john.doe@gmail.com", "John Doe");
        final var eventID = TSID.fast().toLong();

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(eventID, customer.getId());

        final var useCase = new SubscribeCustomerToEventUseCase(customerService, eventService);
        final var exception = assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        assertEquals(expectedError, exception.getMessage());
    }

    @Test
    @DisplayName("Não deve comprar um ticket de um cliente não existente")
    public void testReserveTicketWithoutCustomer() {

        final var expectedError = "Customer not found";
        final var customerID = TSID.fast().toLong();
        final var eventID = TSID.fast().toLong();

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(eventID, customerID);

        final var useCase = new SubscribeCustomerToEventUseCase(customerService, eventService);
        final var exception = assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        assertEquals(expectedError, exception.getMessage());
    }

    @Test
    @DisplayName("Um mesmo cliente não pode comprar dois tickets para o mesmo evento")
    public void testReserveTicketMoreThanOnce() {

        final var aCustomer = createCustomer("12345678900", "john.doe@gmail.com", "John Doe");
        final var aEvent = createEvent("Event test", 10);
        createTicket(aEvent, aCustomer);
        final var expectedError = "Email already registered";

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(aEvent.getId(), aCustomer.getId());

        final var useCase = new SubscribeCustomerToEventUseCase(customerService, eventService);
        final var exception = assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        assertEquals(expectedError, exception.getMessage());
    }

    @Test
    @DisplayName("Um cliente não deve comprar um ticket de um evento esgotado")
    public void testReserveTicketWithoutSlots() {

        final var expectedError = "Event sold out";
        final var aCustomer = createCustomer("12345678900", "john.doe@gmail.com", "John Doe");
        final var aEvent = createEvent("Event test", 0);

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(aEvent.getId(), aCustomer.getId());

        final var useCase = new SubscribeCustomerToEventUseCase(customerService, eventService);
        final var exception = assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        assertEquals(expectedError, exception.getMessage());
    }

    private Customer createCustomer(final String cpf, final String email, final String name) {
        final var customer = new Customer();
        customer.setCpf(cpf);
        customer.setEmail(email);
        customer.setName(name);
        return customerRepository.save(customer);
    }

    private Event createEvent(final String name, final int totalSpots) {
        final var aEvent = new Event();
        aEvent.setName(name);
        aEvent.setTotalSpots(totalSpots);
        aEvent.setTickets(Collections.emptySet());
        return eventRepository.save(aEvent);
    }

    private Ticket createTicket(final Event event, final Customer customer) {
        final var ticket = new Ticket();
        ticket.setEvent(event);
        ticket.setCustomer(customer);
        ticket.setStatus(TicketStatus.PENDING);
        return ticketRepository.save(ticket);
    }
}