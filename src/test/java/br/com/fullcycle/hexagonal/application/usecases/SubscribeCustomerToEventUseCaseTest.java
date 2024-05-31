package br.com.fullcycle.hexagonal.application.usecases;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.infrastructure.models.Customer;
import br.com.fullcycle.hexagonal.infrastructure.models.Event;
import br.com.fullcycle.hexagonal.infrastructure.models.Ticket;
import br.com.fullcycle.hexagonal.infrastructure.models.TicketStatus;
import br.com.fullcycle.hexagonal.infrastructure.services.CustomerService;
import br.com.fullcycle.hexagonal.infrastructure.services.EventService;
import io.hypersistence.tsid.TSID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class SubscribeCustomerToEventUseCaseTest {


    @Test
    @DisplayName("Deve comprar um ticket de um evento")
    public void testReserveTicket() {

        final var expectedTicketsSize = 1;
        final var customerID = TSID.fast().toLong();
        final var eventID = TSID.fast().toLong();

        final var aEvent = new Event();
        aEvent.setId(eventID);
        aEvent.setName("Disney on Ice");
        aEvent.setTotalSpots(10);

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(aEvent.getId(), customerID);
        final var customerService = Mockito.mock(CustomerService.class);
        final var eventService = Mockito.mock(EventService.class);

        when(customerService.findById(customerID)).thenReturn(Optional.of(new Customer()));
        when(eventService.findById(aEvent.getId())).thenReturn(Optional.of(aEvent));
        when(eventService.findTicketByEventIdAndCustomerId(eventID, customerID)).thenReturn(Optional.empty());
        when(eventService.save(any())).thenAnswer(a -> {
            var event = a.getArgument(0, Event.class);
            assertEquals(expectedTicketsSize, event.getTickets().size());
            return event;
        });

        final var useCase = new SubscribeCustomerToEventUseCase(customerService, eventService);
        final var output = useCase.execute(subscribeInput);

        assertEquals(eventID, output.eventId());
        assertNotNull(output.reservationDate());
        assertEquals(TicketStatus.PENDING.name(), output.ticketStatus());
    }

    @Test
    @DisplayName("Não deve comprar um ticket de um evento que não existe")
    public void testReserveTicketWithoutEvent() {

        final var expectedError = "Event not found";
        final var customerID = TSID.fast().toLong();
        final var eventID = TSID.fast().toLong();

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(eventID, customerID);
        final var customerService = Mockito.mock(CustomerService.class);
        final var eventService = Mockito.mock(EventService.class);

        when(customerService.findById(customerID)).thenReturn(Optional.of(new Customer()));
        when(eventService.findById(eventID)).thenReturn(Optional.empty());

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
        final var customerService = Mockito.mock(CustomerService.class);
        final var eventService = Mockito.mock(EventService.class);

        when(customerService.findById(customerID)).thenReturn(Optional.empty());

        final var useCase = new SubscribeCustomerToEventUseCase(customerService, eventService);
        final var exception = assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        assertEquals(expectedError, exception.getMessage());
    }

    @Test
    @DisplayName("Um mesmo cliente não pode comprar dois tickets para o mesmo evento")
    public void testReserveTicketMoreThanOnce() {

        final var expectedError = "Email already registered";
        final var customerID = TSID.fast().toLong();
        final var eventID = TSID.fast().toLong();

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(eventID, customerID);
        final var customerService = Mockito.mock(CustomerService.class);
        final var eventService = Mockito.mock(EventService.class);

        when(customerService.findById(customerID)).thenReturn(Optional.of(new Customer()));
        when(eventService.findById(eventID)).thenReturn(Optional.of(new Event()));
        when(eventService.findTicketByEventIdAndCustomerId(eventID, customerID)).thenReturn(Optional.of(new Ticket()));

        final var useCase = new SubscribeCustomerToEventUseCase(customerService, eventService);
        final var exception = assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        assertEquals(expectedError, exception.getMessage());
    }

    @Test
    @DisplayName("Um cliente não deve comprar um ticket de um evento esgotado")
    public void testReserveTicketWithoutSlots() {

        final var expectedError = "Event sold out";
        final var customerID = TSID.fast().toLong();
        final var eventID = TSID.fast().toLong();

        final var aEvent = new Event();
        aEvent.setId(eventID);
        aEvent.setName("Disney on Ice");
        aEvent.setTotalSpots(0);

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(aEvent.getId(), customerID);
        final var customerService = Mockito.mock(CustomerService.class);
        final var eventService = Mockito.mock(EventService.class);

        when(customerService.findById(customerID)).thenReturn(Optional.of(new Customer()));
        when(eventService.findById(aEvent.getId())).thenReturn(Optional.of(aEvent));
        when(eventService.findTicketByEventIdAndCustomerId(eventID, customerID)).thenReturn(Optional.empty());

        final var useCase = new SubscribeCustomerToEventUseCase(customerService, eventService);
        final var exception = assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        assertEquals(expectedError, exception.getMessage());
    }

}