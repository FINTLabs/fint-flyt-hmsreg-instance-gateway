package no.fintlabs.instance.gateway.models;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import org.springframework.http.MediaType;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
    private String documentDatetime;
    @NotNull
    private MediaType mediatype;
    @NotEmpty
    private String documentBase64;
}