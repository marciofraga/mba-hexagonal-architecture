package br.com.fullcycle.hexagonal.application.domain.event;

import br.com.fullcycle.hexagonal.application.domain.customer.Customer;
import br.com.fullcycle.hexagonal.application.domain.partner.Partner;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.infrastructure.models.TicketStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EventTest {

    @Test
    @DisplayName("Deve criar um evento")
    void testCreate() {

        final var aPartner = Partner
                .create("John Doe", "12.345.678/0001-00", "john.doe@gmail.com");
        final var expectedDate = "2021-01-01";
        final var expectedName = "Disney on Ice";
        final var expectedTotalSpots = 10;
        final var expectedPartnerId = aPartner.getPartnerId().value();
        final var expectedTickets = 0;

        final var actualEvent = Event.create(expectedName, expectedDate, expectedTotalSpots, aPartner);

        assertNotNull(actualEvent.getEventId());
        assertEquals(expectedDate, actualEvent.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        assertEquals(expectedName, actualEvent.getName().value());
        assertEquals(expectedTotalSpots, actualEvent.getTotalSpots());
        assertEquals(expectedPartnerId, actualEvent.getPartnerId().value());
        assertEquals(expectedTickets, actualEvent.getTickets().size());
    }

    @Test
    @DisplayName("Não deve criar um evento com nome invalido")
    void testCreateEventWithInvalidName() {

        final var aPartner = Partner
                .create("John Doe", "12.345.678/0001-00", "john.doe@gmail.com");
        
        final var expectedError = "Invalid value for name";

        final var actualError = assertThrows(ValidationException.class,
                () -> Event.create(null, "2021-01-01", 10, aPartner));

        
        assertEquals(expectedError, actualError.getMessage());
    }

    @Test
    @DisplayName("Não deve criar um evento com data invalida")
    void testCreateEventWithInvalidDate() {

        final var aPartner = Partner
                .create("John Doe", "12.345.678/0001-00", "john.doe@gmail.com");

        final var expectedError = "Invalid date for Event";

        final var actualError = assertThrows(ValidationException.class,
                () -> Event.create("John Doe", "20210101", 10, aPartner));


        assertEquals(expectedError, actualError.getMessage());
    }

    @Test
    @DisplayName("Deve criar um evento com data invalida")
    void testReserveTicket() {

        final var aPartner = Partner
                .create("John Doe", "12.345.678/0001-00", "john.doe@gmail.com");
        
        final var aCustomer = Customer.create("John Doe", "123.456.789-00", "john.doe@gmail.com");

        final var expectedCustomerId = aCustomer.getCustomerId();
        final var expectedDate = "2021-01-01";
        final var expectedName = "Disney on Ice";
        final var expectedTotalSpots = 10;
        final var expectedPartnerId = aPartner.getPartnerId().value();
        final var expectedTickets = 1;
        final var expectedTicketOrder = 1;
        final var expectedTicketStatus = TicketStatus.PENDING;

        final var actualEvent = Event.create(expectedName, expectedDate, expectedTotalSpots, aPartner);
        final var expectedEventId = actualEvent.getEventId();
        
        final var actualTicket = actualEvent.reserveTicket(aCustomer.getCustomerId());

        assertNotNull(actualTicket.ticketId());
        assertNotNull(actualTicket.reservedAt());
        assertNull(actualTicket.paidAt());
        assertEquals(expectedEventId, actualTicket.eventId());
        assertEquals(expectedCustomerId, actualTicket.customerId());
        assertEquals(expectedTicketStatus, actualTicket.status());
        
        assertEquals(expectedDate, actualEvent.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        assertEquals(expectedName, actualEvent.getName().value());
        assertEquals(expectedTotalSpots, actualEvent.getTotalSpots());
        assertEquals(expectedPartnerId, actualEvent.getPartnerId().value());
        assertEquals(expectedTickets, actualEvent.getTickets().size());

        final var actualEventTicket = actualEvent.getTickets().iterator().next();
        assertEquals(expectedTicketOrder, actualEventTicket.getOrdering());
        assertEquals(expectedEventId, actualEventTicket.getEventId());
        assertEquals(expectedCustomerId, actualEventTicket.getCustomerId());
        assertEquals(actualTicket.ticketId(), actualEventTicket.getTicketId());
    }

    @Test
    @DisplayName("Não deve reservar um ticket quando o evento está esgotado")
    void testReserveTicketWhenEventIsSoldOut() {

        final var aPartner = Partner
                .create("John Doe", "12.345.678/0001-00", "john.doe@gmail.com");

        final var aCustomer = Customer.create("John Doe", "123.456.789-00", "john.doe@gmail.com");
        final var aCustomer2 = Customer.create("John Doe", "123.456.789-00", "john1.doe@gmail.com");
        
        final var expectedError = "Event sold out";

        final var actualEvent = Event.create("Disney on Ice", "2021-01-01", 1, aPartner);

        actualEvent.reserveTicket(aCustomer.getCustomerId());
        final var actualError = assertThrows(ValidationException.class,
                () -> actualEvent.reserveTicket(aCustomer2.getCustomerId()));

       
        assertEquals(expectedError, actualError.getMessage());
    }

    @Test
    @DisplayName("Não deve reservar dois tickets para um mesmo cliente")
    void testReserveTicketTwoTicketsForTheSameClient() {

        final var aPartner = Partner
                .create("John Doe", "12.345.678/0001-00", "john.doe@gmail.com");

        final var aCustomer = Customer.create("John Doe", "123.456.789-00", "john.doe@gmail.com");

        final var expectedError = "Email already registered";

        final var actualEvent = Event.create("Disney on Ice", "2021-01-01", 1, aPartner);

        actualEvent.reserveTicket(aCustomer.getCustomerId());
        final var actualError = assertThrows(ValidationException.class,
                () -> actualEvent.reserveTicket(aCustomer.getCustomerId()));


        assertEquals(expectedError, actualError.getMessage());
    }
}
