package no.fintlabs.instance.gateway.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Constraint(validatedBy = ExactlyOneMainDocumentValidator.class)
public @interface ExactlyOneMainDocument {

    String message() default "contains {numberOfMainDocuments} main documents";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}