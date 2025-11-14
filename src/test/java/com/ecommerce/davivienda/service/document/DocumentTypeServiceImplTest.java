package com.ecommerce.davivienda.service.document;

import com.ecommerce.davivienda.dto.document.DocumentTypeResponseDto;
import com.ecommerce.davivienda.entity.user.DocumentType;
import com.ecommerce.davivienda.exception.document.DocumentTypeException;
import com.ecommerce.davivienda.mapper.document.DocumentTypeMapper;
import com.ecommerce.davivienda.repository.user.DocumentTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DocumentTypeServiceImpl - Tests Unitarios")
class DocumentTypeServiceImplTest {

    @Mock
    private DocumentTypeRepository documentTypeRepository;

    @Mock
    private DocumentTypeMapper documentTypeMapper;

    @InjectMocks
    private DocumentTypeServiceImpl documentTypeService;

    private DocumentType mockDocumentType;
    private DocumentTypeResponseDto mockResponseDto;

    @BeforeEach
    void setUp() {
        mockDocumentType = new DocumentType();
        mockDocumentType.setDocumentoId(1);
        mockDocumentType.setCodigo("CC");
        mockDocumentType.setNombre("Cédula de Ciudadanía");

        mockResponseDto = DocumentTypeResponseDto.builder()
                .documentoId(1)
                .codigo("CC")
                .nombre("Cédula de Ciudadanía")
                .build();
    }

    @Test
    @DisplayName("findAll - Retorna lista de tipos de documento")
    void testFindAll_Success() {
        List<DocumentType> documentTypes = Arrays.asList(mockDocumentType);
        List<DocumentTypeResponseDto> responseDtos = Arrays.asList(mockResponseDto);

        when(documentTypeRepository.findAll()).thenReturn(documentTypes);
        when(documentTypeMapper.toResponseDtoList(documentTypes)).thenReturn(responseDtos);

        List<DocumentTypeResponseDto> result = documentTypeService.findAll();

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getCodigo()).isEqualTo("CC");

        verify(documentTypeRepository).findAll();
        verify(documentTypeMapper).toResponseDtoList(documentTypes);
    }

    @Test
    @DisplayName("findByCode - Encontrar tipo de documento por código")
    void testFindByCode_Success() {
        when(documentTypeRepository.findByCodigo("CC")).thenReturn(Optional.of(mockDocumentType));
        when(documentTypeMapper.toResponseDto(mockDocumentType)).thenReturn(mockResponseDto);

        DocumentTypeResponseDto result = documentTypeService.findByCode("CC");

        assertThat(result).isNotNull();
        assertThat(result.getCodigo()).isEqualTo("CC");
        assertThat(result.getNombre()).isEqualTo("Cédula de Ciudadanía");

        verify(documentTypeRepository).findByCodigo("CC");
        verify(documentTypeMapper).toResponseDto(mockDocumentType);
    }

    @Test
    @DisplayName("findByCode - Tipo de documento no encontrado, lanza excepción")
    void testFindByCode_NotFound_ThrowsException() {
        when(documentTypeRepository.findByCodigo("XX")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> documentTypeService.findByCode("XX"))
                .isInstanceOf(DocumentTypeException.class);

        verify(documentTypeRepository).findByCodigo("XX");
        verify(documentTypeMapper, never()).toResponseDto(any());
    }
}

