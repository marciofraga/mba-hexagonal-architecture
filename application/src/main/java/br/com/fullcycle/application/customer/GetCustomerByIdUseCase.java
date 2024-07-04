package br.com.fullcycle.application.customer;

import br.com.fullcycle.application.UseCase;
import br.com.fullcycle.domain.customer.CustomerId;
import br.com.fullcycle.domain.customer.CustomerRepository;

import java.util.Optional;

public class GetCustomerByIdUseCase extends UseCase<GetCustomerByIdUseCase.Input, Optional<GetCustomerByIdUseCase.Output>> {

    private final CustomerRepository customerRepository;

    public GetCustomerByIdUseCase(final CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Optional<Output> execute(Input input) {
        return customerRepository.customerOfId(CustomerId.with(input.id))
                .map(c -> new Output(
                        c.getCustomerId().value(), 
                        c.getCpf().value(), 
                        c.getEmail().value(), 
                        c.getName().value())
                );
    }

    public record Input(String id) {}
    public record Output(String id, String cpf, String email, String name) {}
}
