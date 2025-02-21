package no.fintlabs.instance.gateway.models.caseinfo;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Builder
@Getter
@EqualsAndHashCode
@Jacksonized
public class CaseManager {
    private final String firstName;
    private final String middleName;
    private final String lastName;
    private final String email;
    private final String phone;
}
