package com.ecommerce.davivienda.mapper.role;

import com.ecommerce.davivienda.dto.role.RoleRequestDto;
import com.ecommerce.davivienda.dto.role.RoleResponseDto;
import com.ecommerce.davivienda.entity.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RoleMapper - Tests Unitarios")
class RoleMapperTest {

    private RoleMapper roleMapper;
    private RoleRequestDto mockRequestDto;
    private Role mockRole;

    @BeforeEach
    void setUp() {
        roleMapper = Mappers.getMapper(RoleMapper.class);

        mockRequestDto = RoleRequestDto.builder()
                .nombre("Cliente")
                .build();

        mockRole = new Role();
        mockRole.setRolId(1);
        mockRole.setNombreRol("Cliente");
        mockRole.setNombreRol("Cliente");
    }

    @Test
    @DisplayName("toEntity - Convierte RoleRequestDto a Role")
    void testToEntity() {
        Role result = roleMapper.toEntity(mockRequestDto);

        assertThat(result).isNotNull();
        assertThat(result.getNombreRol()).isEqualTo("Cliente");
    }

    @Test
    @DisplayName("toResponseDto - Convierte Role a RoleResponseDto")
    void testToResponseDto() {
        RoleResponseDto result = roleMapper.toResponseDto(mockRole);

        assertThat(result).isNotNull();
        assertThat(result.getRolId()).isEqualTo(1);
        assertThat(result.getNombre()).isEqualTo("Cliente");
    }

    @Test
    @DisplayName("toResponseDtoList - Convierte lista de Roles")
    void testToResponseDtoList() {
        List<Role> roles = Arrays.asList(mockRole);
        List<RoleResponseDto> result = roleMapper.toResponseDtoList(roles);

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Cliente");
    }

    @Test
    @DisplayName("updateEntityFromDto - Actualiza Role desde RoleRequestDto")
    void testUpdateEntityFromDto() {
        RoleRequestDto updateDto = RoleRequestDto.builder()
                .nombre("Administrador")
                .build();

        roleMapper.updateEntityFromDto(updateDto, mockRole);

        assertThat(mockRole.getNombreRol()).isEqualTo("Administrador");
        assertThat(mockRole.getRolId()).isEqualTo(1); // No cambia
    }
}

