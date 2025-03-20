package no.fintlabs.gateway.webinstance.validation;

import no.fintlabs.gateway.webinstance.TestCaseUtils;
import no.fintlabs.gateway.webinstance.models.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import jakarta.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExactlyOneMainDocumentValidatorTest {

    private ExactlyOneMainDocumentValidator exactlyOneMainDocumentValidator;
    private final TestCaseUtils testCaseUtils = new TestCaseUtils();

    @Mock
    ConstraintValidatorContext constraintValidatorContext;

    @BeforeEach
    public void setup() {
        exactlyOneMainDocumentValidator = new ExactlyOneMainDocumentValidator();
    }

    @Test
    public void givenDocumentsWithOneMainDocumentShouldReturnTrue() {
        List<Document> documents = testCaseUtils.createDocuments();
        boolean valid = exactlyOneMainDocumentValidator.isValid(documents, constraintValidatorContext);
        assertTrue(valid);
    }

    @Test
    public void givenDocumentsWithNoMainDocumentsShouldReturnFalse() {
        List<Document> documents = new ArrayList<>();
        documents.add(testCaseUtils.createOtherDocument1());
        documents.add(testCaseUtils.createOtherDocument2());

        boolean valid = exactlyOneMainDocumentValidator.isValid(documents, constraintValidatorContext);
        assertFalse(valid);
    }

    @Test
    public void givenDocumentsWithMoreThanOneMainDocumentShouldReturnFalse() {
        List<Document> documents = new ArrayList<>();
        documents.add(testCaseUtils.createMainDocument());
        documents.add(testCaseUtils.createMainDocument());
        documents.add(testCaseUtils.createOtherDocument1());

        boolean valid = exactlyOneMainDocumentValidator.isValid(documents, constraintValidatorContext);
        assertFalse(valid);
    }

}