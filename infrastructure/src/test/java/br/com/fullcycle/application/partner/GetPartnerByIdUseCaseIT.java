package br.com.fullcycle.application.partner;

import br.com.fullcycle.AbstractIntegrationTest;
import br.com.fullcycle.domain.partner.Partner;
import br.com.fullcycle.domain.partner.PartnerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

class GetPartnerByIdUseCaseIT extends AbstractIntegrationTest {

    @Autowired
    private GetPartnerByIdUseCase useCase;
    @Autowired
    private PartnerRepository partnerRepository;

    @BeforeEach
    void tearDown() {
        partnerRepository.deleteAll();
    }
    
    @Test
    @DisplayName("Deve obter um parceiro por id")
    void testGetById() {
        final var expectedCNPJ = "12.345.678/0001-00";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";

        final var aPartner = createPartner(expectedCNPJ, expectedEmail, expectedName);

        final var input = new GetPartnerByIdUseCase.Input(aPartner.getPartnerId().value());
        final var output = useCase.execute(input).get();
        // then
        Assertions.assertEquals(aPartner.getPartnerId().value(), output.id());
        Assertions.assertEquals(expectedCNPJ, output.cnpj());
        Assertions.assertEquals(expectedEmail, output.email());
        Assertions.assertEquals(expectedName, output.name());
    }

    @Test
    @DisplayName("Deve obter vazio ao tentar recuperar um cliente não existente por id")
    void testGetByIdWIthInvalidId() {
        // given
        final var expectedID = UUID.randomUUID().toString();

        final var input = new GetPartnerByIdUseCase.Input(expectedID);
        final var output = useCase.execute(input);

        // then
        Assertions.assertTrue(output.isEmpty());
    }

    private Partner createPartner(String cnpj, String email, String name) {
        return partnerRepository.create(Partner.create(name, cnpj, email));
    }
}