package no.fintlabs.instance.gateway.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Jacksonized
@EqualsAndHashCode
@Builder
public class CaseInstance {
    @NotNull
    @JsonProperty("organisasjonsnavn")
    private final String organizationName;
    @NotNull
    private final String instanceid;
    @NotNull
    @JsonProperty("organisasjonsnr")
    private final String organizationNumber;
    @NotNull
    @JsonProperty("prosjektnavn")
    private final String projectName;
    @NotNull
    @JsonProperty("hovedleverandor")
    private final String mainSupplier;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS")
    @JsonProperty("behandlet")
    private LocalDateTime processed;
    @NotNull
    private final String status;

    @JsonProperty("files")
    private final List<@Valid @NotNull Document> documents;
}
