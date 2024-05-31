package br.com.fullcycle.hexagonal.application.usecases;

import br.com.fullcycle.hexagonal.AbstractIntegrationTest;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.infrastructure.Main;
import br.com.fullcycle.hexagonal.infrastructure.models.Partner;
import br.com.fullcycle.hexagonal.infrastructure.repositories.EventRepository;
import br.com.fullcycle.hexagonal.infrastructure.repositories.PartnerRepository;
import io.hypersistence.tsid.TSID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateEventUseCaseIT extends AbstractIntegrationTest {

    @Autowired
    private CreateEventUseCase useCase;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private PartnerRepository partnerRepository;

    @BeforeEach
    void tearDown() {
        eventRepository.deleteAll();
        partnerRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar um evento")
    void testCreate() {

        final var partner = createPartner("12345678900", "john.doe@gmail.com", "John Doe");
        final var expectedDate = "2021-01-01";
        final var expectedName = "Disney on Ice";
        final var expectedTotalSpots = 10;
        final var expectedPartnerId = partner.getId();

        final var createInput = new CreateEventUseCase.Input(expectedDate, expectedName, expectedPartnerId, expectedTotalSpots);
        final var output = useCase.execute(createInput);

        assertNotNull(output.id());
        assertEquals(expectedDate, output.date());
        assertEquals(expectedName, output.name());
        assertEquals(expectedTotalSpots, output.totalSpots());
        assertEquals(expectedPartnerId, output.id());

    }

    @Test
    @DisplayName("Não deve criar um evento quando um partner não for encontrado")
    void shouldThrowErrorWhenPartnerDoesntExists() {

        final var expectedDate = "2021-01-01";
        final var expectedName = "Disney on Ice";
        final var expectedTotalSpots = 10;
        final var expectedPartnerId = TSID.fast().toLong();
        final var expectedError = "Partner not found";

        final var createInput = new CreateEventUseCase.Input(expectedDate, expectedName, expectedPartnerId, expectedTotalSpots);
        final var actualError = assertThrows(ValidationException.class, () -> useCase.execute(createInput));

        assertEquals(expectedError, actualError.getMessage());


    }

    private Partner createPartner(final String cnpj, final String email, final String name) {
        final var partner = new Partner();
        partner.setCnpj(cnpj);
        partner.setEmail(email);
        partner.setName(name);
        return partnerRepository.save(partner);
    }
}