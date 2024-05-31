package br.com.fullcycle.hexagonal.application.usecases;

import br.com.fullcycle.hexagonal.AbstractIntegrationTest;
import br.com.fullcycle.hexagonal.infrastructure.models.Customer;
import br.com.fullcycle.hexagonal.infrastructure.repositories.CustomerRepository;
import br.com.fullcycle.hexagonal.infrastructure.services.CustomerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

class GetCustomerByIdUseCaseIT extends AbstractIntegrationTest {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private CustomerRepository customerRepository;



    @Test
    @DisplayName("Deve obter um cliente por id")
    public void testGetById() {
        final var expectedCPF = "123.456.789-01";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";

        final var aCustomer = createCustomer(expectedCPF, expectedEmail, expectedName);

        final var input = new GetCustomerByIdUseCase.Input(aCustomer.getId());

        final var useCase = new GetCustomerByIdUseCase(customerService);
        final var output = useCase.execute(input).get();
        // then
        Assertions.assertEquals(aCustomer.getId(), output.id());
        Assertions.assertEquals(expectedCPF, output.cpf());
        Assertions.assertEquals(expectedEmail, output.email());
        Assertions.assertEquals(expectedName, output.name());
    }

    @Test
    @DisplayName("Deve obter vazio ao tentar recuperar um cliente não existente por id")
    public void testGetByIdWIthInvalidId() {
        // given
        final var expectedID = UUID.randomUUID().getMostSignificantBits();

        final var input = new GetCustomerByIdUseCase.Input(expectedID);

        final var useCase = new GetCustomerByIdUseCase(customerService);
        final var output = useCase.execute(input);

        // then
        Assertions.assertTrue(output.isEmpty());
    }

    private Customer createCustomer(String expectedCPF, String expectedEmail, String expectedName) {
        final var customer = new Customer();
        customer.setCpf(expectedCPF);
        customer.setEmail(expectedEmail);
        customer.setName(expectedName);
        return customerRepository.save(customer);
    }
}