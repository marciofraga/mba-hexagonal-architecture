package br.com.fullcycle.infrastructure.job;

import br.com.fullcycle.infrastructure.gateway.QueueGateway;
import br.com.fullcycle.infrastructure.jpa.repositories.OutboxJpaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboxRelay {
    
    private final OutboxJpaRepository outboxJpaRepository;
    private final QueueGateway queueGateway;
    
    public OutboxRelay(OutboxJpaRepository outboxJpaRepository, QueueGateway queueGateway) {
        this.outboxJpaRepository = outboxJpaRepository;
        this.queueGateway = queueGateway;
    }
    
    @Scheduled(fixedRate = 2_000)
    @Transactional
    void execute() {
        this.outboxJpaRepository.findTop100ByPublishedFalse()
                .forEach(it -> {
                    this.queueGateway.publish(it.getContent());
                    this.outboxJpaRepository.save(it.notePublished());
                });
    }
}
