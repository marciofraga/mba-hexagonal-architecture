package br.com.fullcycle.hexagonal.application.usecases;

import br.com.fullcycle.hexagonal.AbstractIntegrationTest;
import br.com.fullcycle.hexagonal.infrastructure.models.Partner;
import br.com.fullcycle.hexagonal.infrastructure.repositories.PartnerRepository;
import br.com.fullcycle.hexagonal.infrastructure.services.PartnerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

class GetPartnerByIdUseCaseIT extends AbstractIntegrationTest {

    @Autowired
    private PartnerService partnerService;
    @Autowired
    private PartnerRepository partnerRepository;

    @Test
    @DisplayName("Deve obter um parceiro por id")
    public void testGetById() {
        final var expectedCNPJ = "123.456.789-01";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";

        final var aPartner = createPartner(expectedCNPJ, expectedEmail, expectedName);

        final var input = new GetPartnerByIdUseCase.Input(aPartner.getId());

        final var useCase = new GetPartnerByIdUseCase(partnerService);
        final var output = useCase.execute(input).get();
        // then
        Assertions.assertEquals(aPartner.getId(), output.id());
        Assertions.assertEquals(expectedCNPJ, output.cnpj());
        Assertions.assertEquals(expectedEmail, output.email());
        Assertions.assertEquals(expectedName, output.name());
    }

    @Test
    @DisplayName("Deve obter vazio ao tentar recuperar um cliente não existente por id")
    public void testGetByIdWIthInvalidId() {
        // given
        final var expectedID = UUID.randomUUID().getMostSignificantBits();

        final var input = new GetPartnerByIdUseCase.Input(expectedID);

        final var useCase = new GetPartnerByIdUseCase(partnerService);
        final var output = useCase.execute(input);

        // then
        Assertions.assertTrue(output.isEmpty());
    }

    private Partner createPartner(String cnpj, String email, String name) {
        final var partner = new Partner();
        partner.setCnpj(cnpj);
        partner.setEmail(email);
        partner.setName(name);
        return partnerRepository.save(partner);
    }
}