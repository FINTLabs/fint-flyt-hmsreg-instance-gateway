package no.fintlabs.instance.gateway.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import org.springframework.http.MediaType;

import javax.validation.constraints.NotNull;

@Getter
@Jacksonized
@EqualsAndHashCode
@Builder
public class Document {
    @NotNull
    private String filename;
    @NotNull
    @JsonProperty("ismaindocument")
    private Boolean isMainDocument;
    @NotNull
    @JsonProperty("tittel")
    private String title;
    @NotNull
    @JsonProperty("mediatype")
    private MediaType mediaType;
    @NotNull
    @JsonProperty("documentbase64")
    private String documentBase64;
}