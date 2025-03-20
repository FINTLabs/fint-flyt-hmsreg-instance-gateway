package no.fintlabs.gateway.webinstance;

import no.fintlabs.gateway.webinstance.models.CaseInstance;
import no.fintlabs.gateway.webinstance.models.CaseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static no.fintlabs.webresourceserver.UrlPaths.EXTERNAL_API;

@RestController
@RequestMapping(EXTERNAL_API + "/hmsreg/instances/sak")
public class InstanceController {

    private final InstanceProcessor<CaseInstance> caseInstanceProcessor;
    private final CaseStatusService caseStatusService;

    public InstanceController(
            InstanceProcessor<CaseInstance> caseInstanceProcessor,
            CaseStatusService caseStatusService
    ) {
        this.caseInstanceProcessor = caseInstanceProcessor;
        this.caseStatusService = caseStatusService;
    }

    @GetMapping("{sourceApplicationInstanceId}/status")
    public ResponseEntity<CaseStatus> getInstanceCaseInfo(
            @AuthenticationPrincipal Authentication authentication,
            @PathVariable("sourceApplicationInstanceId") String sourceApplicationInstanceId
    ) {
        return getCaseStatus(authentication, sourceApplicationInstanceId);
    }

    public ResponseEntity<CaseStatus> getCaseStatus(
            Authentication authentication,
            String sourceApplicationInstanceId
    ) {
        return caseStatusService.getCaseStatus(authentication, sourceApplicationInstanceId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("Case with sourceApplicationInstanceId=%s could not be found", sourceApplicationInstanceId)
                ));
    }

    @PostMapping
    public ResponseEntity<?> postIncomingInstanceWithCollectionElements(
            @RequestBody CaseInstance caseInstance,
            @AuthenticationPrincipal Authentication authentication
    ) {
        return caseInstanceProcessor.processInstance(authentication, caseInstance);
    }

}
