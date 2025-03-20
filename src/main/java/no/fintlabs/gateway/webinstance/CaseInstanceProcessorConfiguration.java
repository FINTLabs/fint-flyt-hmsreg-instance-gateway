package no.fintlabs.gateway.webinstance;

import no.fintlabs.gateway.webinstance.mapping.CaseInstanceMappingService;
import no.fintlabs.gateway.webinstance.models.CaseInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class CaseInstanceProcessorConfiguration {

    @Bean
    public InstanceProcessor<CaseInstance> caseInstanceProcessor(
            InstanceProcessorFactoryService instanceProcessorFactoryService,
            CaseInstanceMappingService caseInstanceMappingService
    ) {
        return instanceProcessorFactoryService.createInstanceProcessor(
                "sak",
                (CaseInstance ci) -> Optional.of(ci.getInstanceId()),
                caseInstanceMappingService
        );
    }

}
