package no.fintlabs.instance.gateway;

import no.fintlabs.gateway.instance.ArchiveCaseService;
import no.fintlabs.gateway.instance.InstanceProcessor;
import no.fintlabs.instance.gateway.mapping.CaseInfoMappingService;
import no.fintlabs.instance.gateway.models.CaseInstance;
import no.fintlabs.instance.gateway.models.caseinfo.CaseInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import static no.fintlabs.resourceserver.UrlPaths.EXTERNAL_API;

@RestController
@RequestMapping(EXTERNAL_API + "/hmsreg/instances/sak")
public class InstanceController {

    private final InstanceProcessor<CaseInstance> caseInstanceProcessor;
    private final ArchiveCaseService archiveCaseService;
    private final CaseInfoMappingService caseInfoMappingService;

    public InstanceController(
            InstanceProcessor<CaseInstance> caseInstanceProcessor,
            ArchiveCaseService archiveCaseService,
            CaseInfoMappingService caseInfoMappingService
    ) {
        this.caseInstanceProcessor = caseInstanceProcessor;
        this.archiveCaseService = archiveCaseService;
        this.caseInfoMappingService = caseInfoMappingService;
    }

    @GetMapping("{sourceApplicationInstanceId}")
    public Mono<ResponseEntity<CaseInfo>> getInstanceCaseInfo(
            @AuthenticationPrincipal Mono<Authentication> authenticationMono,
            @PathVariable String sourceApplicationInstanceId
    ) {
        return authenticationMono.map(authentication -> getCaseInfo(
                authentication,
                sourceApplicationInstanceId
        ));
    }

    public ResponseEntity<CaseInfo> getCaseInfo(
            Authentication authentication,
            String sourceApplicationInstanceId
    ) {
        return archiveCaseService.getCase(authentication, sourceApplicationInstanceId)
                .map(caseResource -> caseInfoMappingService.toCaseInfo(sourceApplicationInstanceId, caseResource))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("Case with sourceApplicationInstanceId=%s could not be found", sourceApplicationInstanceId)
                ));
    }

    @PostMapping
    public Mono<ResponseEntity<?>> postIncomingInstanceWithCollectionElements(
            @RequestBody CaseInstance caseInstance,
            @AuthenticationPrincipal Mono<Authentication> authenticationMono
    ) {
        return authenticationMono.flatMap(
                authentication -> caseInstanceProcessor.processInstance(
                        authentication,
                        caseInstance
                )
        );
    }

}
