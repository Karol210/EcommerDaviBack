package com.ecommerce.davivienda.service.user.validation.document;

import com.ecommerce.davivienda.entity.user.DocumentType;
import com.ecommerce.davivienda.exception.user.UserException;
import com.ecommerce.davivienda.service.user.transactional.document.UserDocumentTransactionalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDocumentValidationServiceImpl - Tests Unitarios")
class UserDocumentValidationServiceImplTest {

    @Mock
    private UserDocumentTransactionalService transactionalService;

    @InjectMocks
    private UserDocumentValidationServiceImpl validationService;

    private DocumentType mockDocumentType;

    @BeforeEach
    void setUp() {
        mockDocumentType = new DocumentType();
        mockDocumentType.setDocumentoId(1);
        mockDocumentType.setCodigo("CC");
        mockDocumentType.setNombre("Cédula");
    }

    @Test
    @DisplayName("validateDocumentType - DocumentType existe, retorna documento")
    void testValidateDocumentType_Success() {
        when(transactionalService.findDocumentTypeById(1)).thenReturn(Optional.of(mockDocumentType));

        DocumentType result = validationService.validateDocumentType(1);

        assertThat(result).isNotNull();
        assertThat(result.getDocumentoId()).isEqualTo(1);
    }

    @Test
    @DisplayName("validateDocumentType - DocumentType no encontrado, lanza excepción")
    void testValidateDocumentType_NotFound_ThrowsException() {
        when(transactionalService.findDocumentTypeById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> validationService.validateDocumentType(999))
                .isInstanceOf(UserException.class);
    }

    @Test
    @DisplayName("validateDocumentTypeByName - DocumentType por nombre existe")
    void testValidateDocumentTypeByName_Success() {
        when(transactionalService.findDocumentTypeByNameOrCode("CC")).thenReturn(Optional.of(mockDocumentType));

        DocumentType result = validationService.validateDocumentTypeByName("CC");

        assertThat(result).isNotNull();
        assertThat(result.getCodigo()).isEqualTo("CC");
    }

    @Test
    @DisplayName("validateDocumentTypeByName - DocumentType no encontrado, lanza excepción")
    void testValidateDocumentTypeByName_NotFound_ThrowsException() {
        when(transactionalService.findDocumentTypeByNameOrCode("XX")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> validationService.validateDocumentTypeByName("XX"))
                .isInstanceOf(UserException.class);
    }

    @Test
    @DisplayName("getUpdatedDocumentType - RequestId null, retorna current")
    void testGetUpdatedDocumentType_NullId_ReturnsCurrent() {
        DocumentType result = validationService.getUpdatedDocumentType(null, mockDocumentType);

        assertThat(result).isEqualTo(mockDocumentType);
    }

    @Test
    @DisplayName("getUpdatedDocumentType - RequestId válido, retorna nuevo")
    void testGetUpdatedDocumentType_ValidId_ReturnsNew() {
        DocumentType newDocumentType = new DocumentType();
        newDocumentType.setDocumentoId(2);

        when(transactionalService.findDocumentTypeById(2)).thenReturn(Optional.of(newDocumentType));

        DocumentType result = validationService.getUpdatedDocumentType(2, mockDocumentType);

        assertThat(result).isEqualTo(newDocumentType);
        assertThat(result.getDocumentoId()).isEqualTo(2);
    }
}

