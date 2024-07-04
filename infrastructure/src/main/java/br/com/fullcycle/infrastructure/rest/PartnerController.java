package br.com.fullcycle.infrastructure.rest;

import br.com.fullcycle.application.partner.CreatePartnerUseCase;
import br.com.fullcycle.application.partner.GetPartnerByIdUseCase;
import br.com.fullcycle.domain.exceptions.ValidationException;
import br.com.fullcycle.infrastructure.dtos.NewPartnerDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping(value = "partners")
public class PartnerController {

    private final CreatePartnerUseCase createPartnerUseCase;
    private final GetPartnerByIdUseCase getPartnerByIdUseCase;

    public PartnerController(
            CreatePartnerUseCase createPartnerUseCase, 
            GetPartnerByIdUseCase getPartnerByIdUseCase
    ) {
        this.createPartnerUseCase = createPartnerUseCase;
        this.getPartnerByIdUseCase = getPartnerByIdUseCase;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody NewPartnerDTO dto) {
        try {
            final var output = createPartnerUseCase
                    .execute(new CreatePartnerUseCase.Input(dto.cnpj(), dto.email(), dto.name()));
            return ResponseEntity.created(URI.create("/partners/" + output.id())).body(output);
        } catch(ValidationException e) {
            return ResponseEntity.unprocessableEntity().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable String id) {
        return getPartnerByIdUseCase
                .execute(new GetPartnerByIdUseCase.Input(id))
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

}
