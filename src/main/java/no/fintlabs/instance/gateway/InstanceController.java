package no.fintlabs.instance.gateway;

import no.fintlabs.gateway.instance.InstanceProcessor;
import no.fintlabs.instance.gateway.models.CaseInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static no.fintlabs.resourceserver.UrlPaths.EXTERNAL_API;

@RestController
@RequestMapping(EXTERNAL_API + "/hmsreg/instances")
public class InstanceController {

    private final InstanceProcessor<CaseInstance> caseInstanceProcessor;

    public InstanceController(
            InstanceProcessor<CaseInstance> caseInstanceProcessor
    ) {
        this.caseInstanceProcessor = caseInstanceProcessor;
    }

    @PostMapping("sak")
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
