package br.com.fullcycle.application.partner;

import br.com.fullcycle.application.UseCase;
import br.com.fullcycle.domain.partner.PartnerId;
import br.com.fullcycle.domain.partner.PartnerRepository;

import java.util.Optional;

public class GetPartnerByIdUseCase extends UseCase<GetPartnerByIdUseCase.Input, Optional<GetPartnerByIdUseCase.Output>> {

    private final PartnerRepository partnerRepository;

    public GetPartnerByIdUseCase(PartnerRepository partnerRepository) {
        this.partnerRepository = partnerRepository;
    }

    @Override
    public Optional<Output> execute(Input input) {
        return partnerRepository.partnerOfId(PartnerId.with(input.id))
                .map(o -> new Output(
                        o.getPartnerId().value(), 
                        o.getCnpj().value(), 
                        o.getEmail().value(), 
                        o.getName().value()
                ));
    }

    public record Input(String id) {}
    public record Output(String id, String cnpj, String email, String name) {}
}
