package no.fintlabs.instance.gateway;

import no.fintlabs.gateway.instance.InstanceProcessor;
import no.fintlabs.gateway.instance.InstanceProcessorFactoryService;
import no.fintlabs.instance.gateway.models.CaseInstance;
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
                caseInstance -> Optional.ofNullable(
                        caseInstance.getInstanceId()
                ),
                caseInstanceMappingService
        );
    }

}
