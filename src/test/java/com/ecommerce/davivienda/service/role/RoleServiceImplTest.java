package com.ecommerce.davivienda.service.role;

import com.ecommerce.davivienda.dto.role.RoleResponseDto;
import com.ecommerce.davivienda.entity.user.Role;
import com.ecommerce.davivienda.exception.role.RoleException;
import com.ecommerce.davivienda.mapper.role.RoleMapper;
import com.ecommerce.davivienda.repository.user.RoleRepository;
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
@DisplayName("RoleServiceImpl - Tests Unitarios")
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role mockRole;
    private RoleResponseDto mockResponseDto;

    @BeforeEach
    void setUp() {
        mockRole = new Role();
        mockRole.setRolId(1);
        mockRole.setNombreRol("Cliente");
        mockRole.setNombreRol("Cliente");

        mockResponseDto = RoleResponseDto.builder()
                .rolId(1)
                .nombre("Cliente")
                .nombre("Cliente")
                .build();
    }

    @Test
    @DisplayName("findAll - Retorna lista de roles")
    void testFindAll_Success() {
        List<Role> roles = Arrays.asList(mockRole);
        List<RoleResponseDto> responseDtos = Arrays.asList(mockResponseDto);

        when(roleRepository.findAll()).thenReturn(roles);
        when(roleMapper.toResponseDtoList(roles)).thenReturn(responseDtos);

        List<RoleResponseDto> result = roleService.findAll();

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Cliente");

        verify(roleRepository).findAll();
        verify(roleMapper).toResponseDtoList(roles);
    }

    @Test
    @DisplayName("findByName - Encontrar rol por nombre")
    void testFindByName_Success() {
        when(roleRepository.findByNombreRol("Cliente")).thenReturn(Optional.of(mockRole));
        when(roleMapper.toResponseDto(mockRole)).thenReturn(mockResponseDto);

        RoleResponseDto result = roleService.findByName("Cliente");

        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("Cliente");

        verify(roleRepository).findByNombreRol("Cliente");
        verify(roleMapper).toResponseDto(mockRole);
    }

    @Test
    @DisplayName("findByName - Rol no encontrado, lanza excepción")
    void testFindByName_NotFound_ThrowsException() {
        when(roleRepository.findByNombreRol("NoExiste")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.findByName("NoExiste"))
                .isInstanceOf(RoleException.class);

        verify(roleRepository).findByNombreRol("NoExiste");
        verify(roleMapper, never()).toResponseDto(any());
    }
}

