package br.com.fullcycle.application.customer;

import br.com.fullcycle.AbstractIntegrationTest;
import br.com.fullcycle.domain.customer.Customer;
import br.com.fullcycle.domain.customer.CustomerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

class GetCustomerByIdUseCaseIT extends AbstractIntegrationTest {

    @Autowired
    private GetCustomerByIdUseCase useCase;
    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void tearDown() {
        customerRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve obter um cliente por id")
    void testGetById() {
        final var expectedCPF = "123.456.789-01";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";

        final var aCustomer = createCustomer(expectedCPF, expectedEmail, expectedName);

        final var input = new GetCustomerByIdUseCase.Input(aCustomer.getCustomerId().value());
        final var output = useCase.execute(input).get();
        // then
        Assertions.assertEquals(aCustomer.getCustomerId().value(), output.id());
        Assertions.assertEquals(expectedCPF, output.cpf());
        Assertions.assertEquals(expectedEmail, output.email());
        Assertions.assertEquals(expectedName, output.name());
    }

    @Test
    @DisplayName("Deve obter vazio ao tentar recuperar um cliente não existente por id")
    void testGetByIdWIthInvalidId() {
        // given
        final var expectedID = UUID.randomUUID().toString();

        final var input = new GetCustomerByIdUseCase.Input(expectedID);
        final var output = useCase.execute(input);

        // then
        Assertions.assertTrue(output.isEmpty());
    }

    private Customer createCustomer(String expectedCPF, String expectedEmail, String expectedName) {
        return customerRepository.create(Customer.create(expectedName, expectedCPF, expectedEmail));
    }
}