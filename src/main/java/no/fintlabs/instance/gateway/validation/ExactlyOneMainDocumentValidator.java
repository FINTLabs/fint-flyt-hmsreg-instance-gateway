package no.fintlabs.instance.gateway.validation;

import no.fintlabs.instance.gateway.models.Document;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;

public class ExactlyOneMainDocumentValidator implements ConstraintValidator<ExactlyOneMainDocument, Collection<Document>> {

    @Override
    public boolean isValid(Collection<Document> documents, ConstraintValidatorContext constraintValidatorContext) {
        long numberOfMainDocuments = documents.stream()
                .filter(Document::getIsMainDocument)
                .count();
        if (numberOfMainDocuments == 1) {
            return true;
        }
        if (constraintValidatorContext instanceof HibernateConstraintValidatorContext) {
            constraintValidatorContext.unwrap(HibernateConstraintValidatorContext.class)
                    .addMessageParameter("numberOfMainDocuments", numberOfMainDocuments);
        }
        return false;
    }

}