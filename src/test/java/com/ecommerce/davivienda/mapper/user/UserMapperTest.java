package com.ecommerce.davivienda.mapper.user;

import com.ecommerce.davivienda.entity.user.*;
import com.ecommerce.davivienda.models.user.UserRequest;
import com.ecommerce.davivienda.models.user.UserResponse;
import com.ecommerce.davivienda.models.user.UserUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserMapper - Tests Unitarios")
class UserMapperTest {

    private UserMapper userMapper;
    private UserRequest mockRequest;
    private User mockUser;
    private DocumentType mockDocumentType;
    private UserStatus mockUserStatus;
    private Role mockRole;

    @BeforeEach
    void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);

        mockDocumentType = new DocumentType();
        mockDocumentType.setDocumentoId(1);
        mockDocumentType.setCodigo("CC");
        mockDocumentType.setNombre("Cédula de Ciudadanía");

        mockUserStatus = new UserStatus();
        mockUserStatus.setEstadoUsuarioId(1);
        mockUserStatus.setNombre("Activo");

        mockRole = new Role();
        mockRole.setRolId(1);
        mockRole.setNombreRol("Cliente");

        mockRequest = UserRequest.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .documentType("CC")
                .documentNumber("123456")
                .email("juan@test.com")
                .password("Password123")
                .roles(Collections.singletonList("Cliente"))
                .build();

        mockUser = new User();
        mockUser.setUsuarioId(1);
        mockUser.setNombre("Juan");
        mockUser.setApellido("Pérez");
        mockUser.setDocumentType(mockDocumentType);
        mockUser.setNumeroDeDoc("123456");
        mockUser.setUserStatus(mockUserStatus);
        mockUser.setUsuarioRolId(1);
        
        Credentials credentials = new Credentials();
        credentials.setCorreo("juan@test.com");
        credentials.setContrasena("hashedPassword");
        mockUser.setCredenciales(credentials);
    }

    @Test
    @DisplayName("toEntity - Convierte UserRequest a User")
    void testToEntity() {
        User result = userMapper.toEntity(mockRequest, mockDocumentType, mockUserStatus, "hashedPassword");

        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("Juan");
        assertThat(result.getApellido()).isEqualTo("Pérez");
        assertThat(result.getDocumentType()).isEqualTo(mockDocumentType);
        assertThat(result.getNumeroDeDoc()).isEqualTo("123456");
        assertThat(result.getUserStatus()).isEqualTo(mockUserStatus);
        assertThat(result.getCredenciales()).isNotNull();
        assertThat(result.getCredenciales().getCorreo()).isEqualTo("juan@test.com");
        assertThat(result.getCredenciales().getContrasena()).isEqualTo("hashedPassword");
    }

    @Test
    @DisplayName("buildCredentials - Construye Credentials correctamente")
    void testBuildCredentials() {
        Credentials result = userMapper.buildCredentials("test@test.com", "hashedPassword");

        assertThat(result).isNotNull();
        assertThat(result.getCorreo()).isEqualTo("test@test.com");
        assertThat(result.getContrasena()).isEqualTo("hashedPassword");
    }

    @Test
    @DisplayName("buildUserRoles - Construye lista de UserRole")
    void testBuildUserRoles() {
        List<Role> roles = Arrays.asList(mockRole);
        List<UserRole> result = userMapper.buildUserRoles(1, roles);

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getUsuarioId()).isEqualTo(1);
        assertThat(result.get(0).getRole()).isEqualTo(mockRole);
    }

    @Test
    @DisplayName("buildUserRoles - Lista vacía retorna lista vacía")
    void testBuildUserRoles_EmptyList() {
        List<UserRole> result = userMapper.buildUserRoles(1, Collections.emptyList());

        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("buildUserRoles - Lista null retorna lista vacía")
    void testBuildUserRoles_NullList() {
        List<UserRole> result = userMapper.buildUserRoles(1, null);

        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("toResponse - Convierte User a UserResponse")
    void testToResponse() {
        UserRole userRole = UserRole.builder()
                .usuarioRolId(1)
                .usuarioId(1)
                .role(mockRole)
                .build();
        mockUser.setRoles(Arrays.asList(userRole));

        UserResponse result = userMapper.toResponse(mockUser);

        assertThat(result).isNotNull();
        assertThat(result.getUsuarioId()).isEqualTo(1);
        assertThat(result.getNombre()).isEqualTo("Juan");
        assertThat(result.getApellido()).isEqualTo("Pérez");
        assertThat(result.getDocumentType()).isEqualTo("Cédula de Ciudadanía");
        assertThat(result.getDocumentNumber()).isEqualTo("123456");
        assertThat(result.getEmail()).isEqualTo("juan@test.com");
        assertThat(result.getStatus()).isEqualTo("Activo");
        assertThat(result.getRoles()).hasSize(1).contains("Cliente");
    }

    @Test
    @DisplayName("toResponse - Usuario null retorna null")
    void testToResponse_NullUser() {
        UserResponse result = userMapper.toResponse(null);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("toResponseList - Convierte lista de Users")
    void testToResponseList() {
        List<User> users = Arrays.asList(mockUser);
        List<UserResponse> result = userMapper.toResponseList(users);

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Juan");
    }

    @Test
    @DisplayName("extractRoleNames - Extrae nombres de roles")
    void testExtractRoleNames() {
        UserRole userRole = UserRole.builder()
                .role(mockRole)
                .build();
        List<UserRole> userRoles = Arrays.asList(userRole);

        List<String> result = userMapper.extractRoleNames(userRoles);

        assertThat(result).isNotNull().hasSize(1).contains("Cliente");
    }

    @Test
    @DisplayName("assignPrimaryRole - Asigna rol primario")
    void testAssignPrimaryRole() {
        UserRole userRole = UserRole.builder()
                .usuarioRolId(100)
                .build();
        List<UserRole> savedUserRoles = Arrays.asList(userRole);

        boolean result = userMapper.assignPrimaryRole(mockUser, savedUserRoles);

        assertThat(result).isTrue();
        assertThat(mockUser.getUsuarioRolId()).isEqualTo(100);
    }

    @Test
    @DisplayName("assignPrimaryRole - Lista vacía retorna false")
    void testAssignPrimaryRole_EmptyList() {
        boolean result = userMapper.assignPrimaryRole(mockUser, Collections.emptyList());

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("updateUserFields - Actualiza campos del usuario")
    void testUpdateUserFields() {
        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .nombre("Carlos")
                .apellido("González")
                .build();

        userMapper.updateUserFields(mockUser, updateRequest, mockDocumentType, mockUserStatus);

        assertThat(mockUser.getNombre()).isEqualTo("Carlos");
        assertThat(mockUser.getApellido()).isEqualTo("González");
    }
}

