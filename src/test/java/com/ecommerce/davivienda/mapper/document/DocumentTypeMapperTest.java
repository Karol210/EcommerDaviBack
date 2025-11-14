package com.ecommerce.davivienda.mapper.document;

import com.ecommerce.davivienda.dto.document.DocumentTypeRequestDto;
import com.ecommerce.davivienda.dto.document.DocumentTypeResponseDto;
import com.ecommerce.davivienda.entity.user.DocumentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DocumentTypeMapper - Tests Unitarios")
class DocumentTypeMapperTest {

    private DocumentTypeMapper documentTypeMapper;
    private DocumentTypeRequestDto mockRequestDto;
    private DocumentType mockDocumentType;

    @BeforeEach
    void setUp() {
        documentTypeMapper = Mappers.getMapper(DocumentTypeMapper.class);

        mockRequestDto = DocumentTypeRequestDto.builder()
                .codigo("CC")
                .nombre("Cédula de Ciudadanía")
                .build();

        mockDocumentType = new DocumentType();
        mockDocumentType.setDocumentoId(1);
        mockDocumentType.setCodigo("CC");
        mockDocumentType.setNombre("Cédula de Ciudadanía");
    }

    @Test
    @DisplayName("toEntity - Convierte DocumentTypeRequestDto a DocumentType")
    void testToEntity() {
        DocumentType result = documentTypeMapper.toEntity(mockRequestDto);

        assertThat(result).isNotNull();
        assertThat(result.getCodigo()).isEqualTo("CC");
        assertThat(result.getNombre()).isEqualTo("Cédula de Ciudadanía");
    }

    @Test
    @DisplayName("toResponseDto - Convierte DocumentType a DocumentTypeResponseDto")
    void testToResponseDto() {
        DocumentTypeResponseDto result = documentTypeMapper.toResponseDto(mockDocumentType);

        assertThat(result).isNotNull();
        assertThat(result.getDocumentoId()).isEqualTo(1);
        assertThat(result.getCodigo()).isEqualTo("CC");
        assertThat(result.getNombre()).isEqualTo("Cédula de Ciudadanía");
    }

    @Test
    @DisplayName("toResponseDtoList - Convierte lista de DocumentTypes")
    void testToResponseDtoList() {
        List<DocumentType> documentTypes = Arrays.asList(mockDocumentType);
        List<DocumentTypeResponseDto> result = documentTypeMapper.toResponseDtoList(documentTypes);

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getCodigo()).isEqualTo("CC");
    }

    @Test
    @DisplayName("updateEntityFromDto - Actualiza DocumentType desde DocumentTypeRequestDto")
    void testUpdateEntityFromDto() {
        DocumentTypeRequestDto updateDto = DocumentTypeRequestDto.builder()
                .codigo("PA")
                .nombre("Pasaporte")
                .build();

        documentTypeMapper.updateEntityFromDto(updateDto, mockDocumentType);

        assertThat(mockDocumentType.getCodigo()).isEqualTo("PA");
        assertThat(mockDocumentType.getNombre()).isEqualTo("Pasaporte");
        assertThat(mockDocumentType.getDocumentoId()).isEqualTo(1); // No cambia
    }
}

