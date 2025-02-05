package no.fintlabs.instance.gateway;

import no.fintlabs.gateway.instance.InstanceMapper;
import no.fintlabs.gateway.instance.model.File;
import no.fintlabs.gateway.instance.model.instance.InstanceObject;
import no.fintlabs.instance.gateway.models.CaseInstance;
import no.fintlabs.instance.gateway.models.Document;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.*;
import java.util.function.Function;

@Service
public class CaseInstanceMappingService implements InstanceMapper<CaseInstance> {

    @Override
    public Mono<InstanceObject> map(
            Long sourceApplicationId,
            CaseInstance caseInstance,
            Function<File, Mono<UUID>> persistFile
    ) {
        Document mainDocument = caseInstance
                .getDocuments()
                .stream()
                .filter(Document::getIsMainDocument)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No main document found"));

        List<Document> attachments = caseInstance
                .getDocuments()
                .stream()
                .filter(document -> !document.getIsMainDocument())
                .toList();

        Mono<Map<String, String>> mainDocumentInstanceValuePerKeyMono = mapMainDocumentToInstanceValuePerKey(
                persistFile,
                sourceApplicationId,
                caseInstance.getInstanceid(),
                mainDocument
        );

        Mono<List<InstanceObject>> attachmentInstanceObjectsMono = mapAttachmentDocumentsToInstanceObjects(
                persistFile,
                sourceApplicationId,
                caseInstance.getInstanceid(),
                attachments
        );

        HashMap<String, String> valuePerKey = getStringStringHashMap(caseInstance);

        return Mono.zip(
                        mainDocumentInstanceValuePerKeyMono,
                        attachmentInstanceObjectsMono
                )
                .map((Tuple2<Map<String, String>, List<InstanceObject>> mainDocumentValuePerKeyAndAttachmentsInstanceObjects) -> {

                            valuePerKey.putAll(mainDocumentValuePerKeyAndAttachmentsInstanceObjects.getT1());
                            return InstanceObject.builder()
                                    .valuePerKey(valuePerKey)
                                    .objectCollectionPerKey(
                                            Map.of(
                                                    "vedlegg", mainDocumentValuePerKeyAndAttachmentsInstanceObjects.getT2()
                                            ))
                                    .build();
                        }

                );
    }

    private static HashMap<String, String> getStringStringHashMap(CaseInstance caseInstance) {
        HashMap<String, String> valuePerKey = new HashMap<>();
        valuePerKey.put("organisasjonsnavn", String.valueOf(caseInstance));
        valuePerKey.put("instanceid", caseInstance.getInstanceid());
        valuePerKey.put("organisasjonsnr", caseInstance.getOrganizationNumber());
        valuePerKey.put("prosjektnavn", caseInstance.getProjectName());
        valuePerKey.put("hovedleverandør", caseInstance.getMainSupplier());
        valuePerKey.put("behandlet", String.valueOf(caseInstance.getProcessed()));
        valuePerKey.put("status", caseInstance.getStatus());
        return valuePerKey;
    }

    private Mono<Map<String, String>> mapMainDocumentToInstanceValuePerKey(
            Function<File, Mono<UUID>> persistFile,
            Long sourceApplicationId,
            String sourceApplicationInstanceId,
            Document document
    ) {
        File file = toFile(
                sourceApplicationId,
                sourceApplicationInstanceId,
                document
        );

        return persistFile.apply(file)
                .map(fileId -> mapMainDocumentAndFileIdToInstanceValuePerKey(document, fileId));
    }

    private Map<String, String> mapMainDocumentAndFileIdToInstanceValuePerKey(
            Document document,
            UUID fileId
    ) {
        return Map.of(
                "tittel", Optional.ofNullable(document.getTitle()).orElse(""),
                "filnavn", Optional.ofNullable(document.getFilename()).orElse(""),
                "mediatype", document.getMediaType().toString(),
                "fil", fileId.toString()
        );
    }

    private Mono<List<InstanceObject>> mapAttachmentDocumentsToInstanceObjects(
            Function<File, Mono<UUID>> persistFile,
            Long sourceApplicationId,
            String sourceApplicationInstanceId,
            List<Document> attachmentDocuments
    ) {
        return Flux.fromIterable(attachmentDocuments)
                .flatMap(attachmentDocument -> mapAttachmentDocumentToInstanceObject(
                        persistFile,
                        sourceApplicationId,
                        sourceApplicationInstanceId,
                        attachmentDocument
                ))
                .collectList();
    }

    private Mono<InstanceObject> mapAttachmentDocumentToInstanceObject(
            Function<File, Mono<UUID>> persistFile,
            Long sourceApplicationId,
            String sourceApplicationInstanceId,
            Document attachmentDocument
    ) {
        File file = toFile(
                sourceApplicationId,
                sourceApplicationInstanceId,
                attachmentDocument
        );
        return persistFile.apply(file)
                .map(fileId -> mapAttachmentDocumentAndFileIdToInstanceObject(
                        attachmentDocument,
                        fileId
                ));
    }

    private InstanceObject mapAttachmentDocumentAndFileIdToInstanceObject(
            Document attachmentDocument,
            UUID fileId
    ) {
        return InstanceObject
                .builder()
                .valuePerKey(Map.of(
                        "title", Optional.ofNullable(attachmentDocument.getTitle()).orElse(""),
                        "filename", Optional.ofNullable(attachmentDocument.getFilename()).orElse(""),
                        "mediatype", attachmentDocument.getMediaType().toString(),
                        "file", fileId.toString()
                ))
                .build();
    }

    private File toFile(
            Long sourceApplicationId,
            String sourceApplicationInstanceId,
            Document document
    ) {
        return File
                .builder()
                .name(document.getFilename())
                .type(document.getMediaType())
                .sourceApplicationId(sourceApplicationId)
                .sourceApplicationInstanceId(sourceApplicationInstanceId)
                .encoding("UTF-8")
                .base64Contents(document.getDocumentBase64())
                .build();
    }

}
