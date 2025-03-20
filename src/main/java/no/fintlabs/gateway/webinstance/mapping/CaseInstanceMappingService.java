package no.fintlabs.gateway.webinstance.mapping;

import no.fintlabs.gateway.webinstance.InstanceMapper;
import no.fintlabs.gateway.webinstance.model.File;
import no.fintlabs.gateway.webinstance.model.instance.InstanceObject;
import no.fintlabs.gateway.webinstance.models.CaseInstance;
import no.fintlabs.gateway.webinstance.models.Document;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

@Service
public class CaseInstanceMappingService implements InstanceMapper<CaseInstance> {

    @NotNull
    @Override
    public InstanceObject map(
            long sourceApplicationId,
            CaseInstance caseInstance,
            @NotNull Function<File, UUID> persistFile
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

        Map<String, String> mainDocumentInstanceValuePerKey = mapMainDocumentToInstanceValuePerKey(
                persistFile,
                sourceApplicationId,
                caseInstance.getInstanceId(),
                mainDocument
        );

        List<InstanceObject> attachmentInstanceObjects = mapAttachmentDocumentsToInstanceObjects(
                persistFile,
                sourceApplicationId,
                caseInstance.getInstanceId(),
                attachments
        );

        HashMap<String, String> valuePerKey = buildBaseValuePerKey(caseInstance);
        valuePerKey.putAll(mainDocumentInstanceValuePerKey);

        return new InstanceObject(
                valuePerKey,
                new HashMap<>(Map.of("vedlegg", attachmentInstanceObjects))
        );

    }

    private static HashMap<String, String> buildBaseValuePerKey(CaseInstance caseInstance) {
        HashMap<String, String> valuePerKey = new HashMap<>();
        valuePerKey.put("organisasjonsNavn", caseInstance.getOrganizationName());
        valuePerKey.put("instansId", caseInstance.getInstanceId());
        valuePerKey.put("organisasjonsNummer", caseInstance.getOrganizationNumber());
        valuePerKey.put("prosjektNavn", caseInstance.getProjectName());
        valuePerKey.put("hovedLeverandor", caseInstance.getMainSupplier());
        valuePerKey.put("behandlet", caseInstance.getProcessed().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS")));
        valuePerKey.put("behandletEpost", caseInstance.getProcessedByEmail());
        valuePerKey.put("status", caseInstance.getStatus());
        valuePerKey.put("type", caseInstance.getType());
        return valuePerKey;
    }

    private Map<String, String> mapMainDocumentToInstanceValuePerKey(
            Function<File, UUID> persistFile,
            Long sourceApplicationId,
            String sourceApplicationInstanceId,
            Document document
    ) {
        File file = toFile(
                sourceApplicationId,
                sourceApplicationInstanceId,
                document
        );

        UUID fileId = persistFile.apply(file);

        return buildMainDocumentKeyValue(document, fileId);
    }

    private Map<String, String> buildMainDocumentKeyValue(
            Document document,
            UUID fileId
    ) {
        return Map.of(
                "hovedDokumentTittel", Optional.ofNullable(document.getTitle()).orElse(""),
                "hovedDokumentFilnavn", Optional.ofNullable(document.getFilename()).orElse(""),
                "hovedDokumentFil", fileId.toString(),
                "hovedDokumentMediatype", document.getMediatype().toString()
        );
    }

    private List<InstanceObject> mapAttachmentDocumentsToInstanceObjects(
            Function<File, UUID> persistFile,
            Long sourceApplicationId,
            String sourceApplicationInstanceId,
            List<Document> attachmentDocuments
    ) {
        List<InstanceObject> results = new ArrayList<>();
        for (Document attachmentDoc : attachmentDocuments) {
            results.add(mapAttachmentDocumentToInstanceObject(
                    persistFile, sourceApplicationId, sourceApplicationInstanceId, attachmentDoc
            ));
        }

        return results;
    }

    private InstanceObject mapAttachmentDocumentToInstanceObject(
            Function<File, UUID> persistFile,
            Long sourceApplicationId,
            String sourceApplicationInstanceId,
            Document attachmentDocument
    ) {
        File file = toFile(
                sourceApplicationId,
                sourceApplicationInstanceId,
                attachmentDocument
        );
        UUID fileId = persistFile.apply(file);
        return buildAttachmentInstanceObject(attachmentDocument, fileId);
    }

    private InstanceObject buildAttachmentInstanceObject(
            Document attachmentDocument,
            UUID fileId
    ) {
        Map<String, String> valuePerKey = Map.of(
                "tittel", Optional.ofNullable(attachmentDocument.getTitle()).orElse(""),
                "filnavn", Optional.ofNullable(attachmentDocument.getFilename()).orElse(""),
                "fil", fileId.toString(),
                "mediatype", attachmentDocument.getMediatype().toString()
        );
        return new InstanceObject(valuePerKey, new HashMap<>());
    }

    private File toFile(
            Long sourceApplicationId,
            String sourceApplicationInstanceId,
            Document document
    ) {
        return new File(
                document.getFilename(),
                sourceApplicationId,
                sourceApplicationInstanceId,
                document.getMediatype(),
                "UTF-8",
                document.getDocumentBase64()
        );
    }

}
