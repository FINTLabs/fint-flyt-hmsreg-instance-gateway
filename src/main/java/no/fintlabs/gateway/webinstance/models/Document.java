package no.fintlabs.gateway.webinstance.models;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import org.springframework.http.MediaType;

@Getter
@Jacksonized
@EqualsAndHashCode
@Builder
public class Document {
    @NotNull
    private String filename;
    @NotNull
    private Boolean isMainDocument;
    @NotNull
    private String title;
    @NotNull
    private MediaType mediatype;
    @NotNull
    private String documentBase64;
}