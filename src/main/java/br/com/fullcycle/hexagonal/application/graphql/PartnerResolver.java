package br.com.fullcycle.hexagonal.application.graphql;

import br.com.fullcycle.hexagonal.application.usecases.CreatePartnerUseCase;
import br.com.fullcycle.hexagonal.application.usecases.GetPartnerByIdUseCase;
import br.com.fullcycle.hexagonal.infrastructure.dtos.CustomerDTO;
import br.com.fullcycle.hexagonal.infrastructure.services.PartnerService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class PartnerResolver {

    private final PartnerService partnerService;

    public PartnerResolver(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

    @MutationMapping
    public CreatePartnerUseCase.Output createPartner(@Argument CustomerDTO input) {
        final var useCase = new CreatePartnerUseCase(partnerService);
        return useCase.execute(new CreatePartnerUseCase.Input(input.getCpf(), input.getEmail(), input.getName()));
    }

    @QueryMapping
    public GetPartnerByIdUseCase.Output partnerOfId(@Argument Long id) {
        final var useCase = new GetPartnerByIdUseCase(partnerService);
        return useCase.execute(new GetPartnerByIdUseCase.Input(id)).orElse(null);
    }
}
