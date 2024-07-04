package br.com.fullcylce.application.customer;

import br.com.fullcycle.application.customer.GetCustomerByIdUseCase;
import br.com.fullcylce.application.repository.InMemoryCustomerRepository;
import br.com.fullcycle.domain.customer.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class GetCustomerByIdUseCaseTest {

    @Test
    @DisplayName("Deve obter um cliente por id")
    void testGetById() {
        final var expectedCPF = "123.456.789-01";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";

        final var aCustomer = Customer.create(expectedName, expectedCPF, expectedEmail);
        final var customerRepository = new InMemoryCustomerRepository();
        customerRepository.create(aCustomer);

        final var expectedID = aCustomer.getCustomerId().value();
        final var input = new GetCustomerByIdUseCase.Input(expectedID);

        final var useCase = new GetCustomerByIdUseCase(customerRepository);
        final var output = useCase.execute(input).get();
        // then
        Assertions.assertEquals(expectedID, output.id());
        Assertions.assertEquals(expectedCPF, output.cpf());
        Assertions.assertEquals(expectedEmail, output.email());
        Assertions.assertEquals(expectedName, output.name());
    }

    @Test
    @DisplayName("Deve obter vazio ao tentar recuperar um cliente não existente por id")
    void testGetByIdWIthInvalidId() {
        final var expectedID = UUID.randomUUID().toString();

        final var input = new GetCustomerByIdUseCase.Input(expectedID);
        final var customerRepository = new InMemoryCustomerRepository();
        final var useCase = new GetCustomerByIdUseCase(customerRepository);
        final var output = useCase.execute(input);
        
        Assertions.assertTrue(output.isEmpty());
    }
}