package com.ecommerce.davivienda.service.user.transactional.document;

import com.ecommerce.davivienda.entity.user.DocumentType;
import com.ecommerce.davivienda.repository.user.DocumentTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDocumentTransactionalServiceImpl - Tests Unitarios")
class UserDocumentTransactionalServiceImplTest {

    @Mock
    private DocumentTypeRepository documentTypeRepository;

    @InjectMocks
    private UserDocumentTransactionalServiceImpl transactionalService;

    private DocumentType mockDocumentType;

    @BeforeEach
    void setUp() {
        mockDocumentType = new DocumentType();
        mockDocumentType.setDocumentoId(1);
        mockDocumentType.setCodigo("CC");
        mockDocumentType.setNombre("Cédula de Ciudadanía");
    }

    @Test
    @DisplayName("findDocumentTypeById - DocumentType existe")
    void testFindDocumentTypeById_Exists() {
        when(documentTypeRepository.findById(1)).thenReturn(Optional.of(mockDocumentType));

        Optional<DocumentType> result = transactionalService.findDocumentTypeById(1);

        assertThat(result).isPresent();
        assertThat(result.get().getDocumentoId()).isEqualTo(1);
        verify(documentTypeRepository).findById(1);
    }

    @Test
    @DisplayName("findDocumentTypeById - DocumentType no existe")
    void testFindDocumentTypeById_NotExists() {
        when(documentTypeRepository.findById(999)).thenReturn(Optional.empty());

        Optional<DocumentType> result = transactionalService.findDocumentTypeById(999);

        assertThat(result).isEmpty();
        verify(documentTypeRepository).findById(999);
    }

    @Test
    @DisplayName("findDocumentTypeByNameOrCode - Encontrado por código")
    void testFindDocumentTypeByNameOrCode_FoundByCode() {
        when(documentTypeRepository.findByCodigo("CC")).thenReturn(Optional.of(mockDocumentType));

        Optional<DocumentType> result = transactionalService.findDocumentTypeByNameOrCode("CC");

        assertThat(result).isPresent();
        assertThat(result.get().getCodigo()).isEqualTo("CC");
        verify(documentTypeRepository).findByCodigo("CC");
        verify(documentTypeRepository, never()).findByNombre(any());
    }

    @Test
    @DisplayName("findDocumentTypeByNameOrCode - Encontrado por nombre")
    void testFindDocumentTypeByNameOrCode_FoundByName() {
        when(documentTypeRepository.findByCodigo("Cédula de Ciudadanía")).thenReturn(Optional.empty());
        when(documentTypeRepository.findByNombre("Cédula de Ciudadanía")).thenReturn(Optional.of(mockDocumentType));

        Optional<DocumentType> result = transactionalService.findDocumentTypeByNameOrCode("Cédula de Ciudadanía");

        assertThat(result).isPresent();
        assertThat(result.get().getNombre()).isEqualTo("Cédula de Ciudadanía");
        verify(documentTypeRepository).findByCodigo("Cédula de Ciudadanía");
        verify(documentTypeRepository).findByNombre("Cédula de Ciudadanía");
    }

    @Test
    @DisplayName("findDocumentTypeByNameOrCode - No encontrado")
    void testFindDocumentTypeByNameOrCode_NotFound() {
        when(documentTypeRepository.findByCodigo("XX")).thenReturn(Optional.empty());
        when(documentTypeRepository.findByNombre("XX")).thenReturn(Optional.empty());

        Optional<DocumentType> result = transactionalService.findDocumentTypeByNameOrCode("XX");

        assertThat(result).isEmpty();
        verify(documentTypeRepository).findByCodigo("XX");
        verify(documentTypeRepository).findByNombre("XX");
    }
}

