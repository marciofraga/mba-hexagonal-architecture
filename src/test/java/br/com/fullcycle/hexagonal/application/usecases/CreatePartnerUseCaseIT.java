package br.com.fullcycle.hexagonal.application.usecases;

import br.com.fullcycle.hexagonal.AbstractIntegrationTest;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.infrastructure.models.Partner;
import br.com.fullcycle.hexagonal.infrastructure.repositories.PartnerRepository;
import br.com.fullcycle.hexagonal.infrastructure.services.PartnerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CreatePartnerUseCaseIT extends AbstractIntegrationTest {

    @Autowired
    private PartnerService partnerService;
    @Autowired
    private PartnerRepository partnerRepository;

    @BeforeEach
    void tearDown() {
        partnerRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar um parceiro")
    public void testCreatePartner() {
        final var expectedCNPJ = "12345678900";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";

        final var createInput = new CreatePartnerUseCase.Input(expectedCNPJ, expectedEmail, expectedName);

        final var useCase = new CreatePartnerUseCase(partnerService);
        final var output = useCase.execute(createInput);

        Assertions.assertNotNull(output.id());
        Assertions.assertEquals(expectedCNPJ, output.cnpj());
        Assertions.assertEquals(expectedEmail, output.email());
        Assertions.assertEquals(expectedName, output.name());
    }

    @Test
    @DisplayName("Não deve cadastrar um parceiro com email duplicado")
    public void testCreateWithDuplicatedCNPJShouldFail() {
        final var expectedCNPJ = "12345678900";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";
        final var expectedError = "Partner already exists";

        final var createInput = new CreatePartnerUseCase.Input(expectedCNPJ, expectedEmail, expectedName);

        createPartner(expectedCNPJ, expectedEmail, expectedName);

        final var useCase = new CreatePartnerUseCase(partnerService);
        final var output = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(createInput));

        Assertions.assertEquals(expectedError, output.getMessage());
    }



    @Test
    @DisplayName("Não deve cadastrar um cliente com e-mail duplicado")
    public void testCreateWithDuplicatedEmailShouldFail() {
        final var expectedCPF = "12345678900";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";
        final var expectedError = "Partner already exists";

        createPartner(expectedCPF, expectedEmail, expectedName);
        final var createInput = new CreatePartnerUseCase.Input(expectedCPF, expectedEmail, expectedName);

        final var useCase = new CreatePartnerUseCase(partnerService);
        final var output = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(createInput));

        Assertions.assertEquals(expectedError, output.getMessage());
    }

    private void createPartner(final String cnpj, final String email, final String name) {
        final var aPartner = new Partner();
        aPartner.setCnpj(cnpj);
        aPartner.setEmail(email);
        aPartner.setName(name);
        partnerRepository.save(aPartner);
    }
}
