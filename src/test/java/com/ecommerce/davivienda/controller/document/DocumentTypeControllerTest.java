package com.ecommerce.davivienda.controller.document;

import com.ecommerce.davivienda.dto.document.DocumentTypeResponseDto;
import com.ecommerce.davivienda.models.Response;
import com.ecommerce.davivienda.service.document.DocumentTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("DocumentTypeController - Tests Unitarios")
class DocumentTypeControllerTest {

    @Mock
    private DocumentTypeService documentTypeService;

    @InjectMocks
    private DocumentTypeController documentTypeController;

    private MockMvc mockMvc;
    private DocumentTypeResponseDto mockDocumentType;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(documentTypeController).build();
        mockDocumentType = DocumentTypeResponseDto.builder()
                .documentoId(1)
                .codigo("CC")
                .nombre("Cédula de Ciudadanía")
                .build();
    }

    @Test
    @DisplayName("GET / - Listar todos los tipos de documento")
    void testFindAll_Success() throws Exception {
        List<DocumentTypeResponseDto> documentTypes = Arrays.asList(mockDocumentType);
        when(documentTypeService.findAll()).thenReturn(documentTypes);

        mockMvc.perform(get("/api/v1/document-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.failure").value(false))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.body").isArray());

        verify(documentTypeService).findAll();
    }

    @Test
    @DisplayName("GET /code/{codigo} - Buscar por código")
    void testFindByCode_Success() throws Exception {
        when(documentTypeService.findByCode("CC")).thenReturn(mockDocumentType);

        mockMvc.perform(get("/api/v1/document-types/code/CC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.failure").value(false))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.body.codigo").value("CC"));

        verify(documentTypeService).findByCode("CC");
    }
}

