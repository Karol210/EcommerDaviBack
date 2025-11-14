package com.ecommerce.davivienda.controller.role;

import com.ecommerce.davivienda.dto.role.RoleResponseDto;
import com.ecommerce.davivienda.models.Response;
import com.ecommerce.davivienda.service.role.RoleService;
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
@DisplayName("RoleController - Tests Unitarios")
class RoleControllerTest {

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    private MockMvc mockMvc;
    private RoleResponseDto mockRole;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(roleController).build();
        mockRole = RoleResponseDto.builder()
                .rolId(1)
                .nombre("Cliente")
                .nombre("Cliente")
                .build();
    }

    @Test
    @DisplayName("GET / - Listar todos los roles")
    void testFindAll_Success() throws Exception {
        List<RoleResponseDto> roles = Arrays.asList(mockRole);
        when(roleService.findAll()).thenReturn(roles);

        mockMvc.perform(get("/api/v1/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.failure").value(false))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.body").isArray());

        verify(roleService).findAll();
    }

    @Test
    @DisplayName("GET /name/{nombre} - Buscar por nombre")
    void testFindByName_Success() throws Exception {
        when(roleService.findByName("Cliente")).thenReturn(mockRole);

        mockMvc.perform(get("/api/v1/roles/name/Cliente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.failure").value(false))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.body.nombre").value("Cliente"));

        verify(roleService).findByName("Cliente");
    }
}

