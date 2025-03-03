package no.fintlabs.instance.gateway.mapping;

import no.fintlabs.gateway.instance.kafka.ArchiveCaseIdRequestService;
import no.fintlabs.instance.gateway.models.CaseStatus;
import no.fintlabs.resourceserver.security.client.sourceapplication.SourceApplicationAuthorizationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CaseStatusService {

    private final SourceApplicationAuthorizationService sourceApplicationAuthorizationService;
    private final ArchiveCaseIdRequestService archiveCaseIdRequestService;

    public CaseStatusService(
            SourceApplicationAuthorizationService sourceApplicationAuthorizationService,
            ArchiveCaseIdRequestService archiveCaseIdRequestService
    ) {
        this.sourceApplicationAuthorizationService = sourceApplicationAuthorizationService;
        this.archiveCaseIdRequestService = archiveCaseIdRequestService;
    }

    public Optional<CaseStatus> getCaseStatus(
            Authentication authentication,
            String sourceApplicationInstanceId
    ) {
        Long sourceApplicationId = sourceApplicationAuthorizationService.getSourceApplicationId(authentication);
        return archiveCaseIdRequestService.getArchiveCaseId(sourceApplicationId, sourceApplicationInstanceId)
                .map(this::toCaseStatus);
    }

    private CaseStatus toCaseStatus(String archiveCaseId) {
        return CaseStatus.builder().archiveCaseId(archiveCaseId).build();
    }

}
