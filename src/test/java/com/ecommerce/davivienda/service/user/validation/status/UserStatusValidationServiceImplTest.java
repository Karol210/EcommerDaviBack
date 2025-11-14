package com.ecommerce.davivienda.service.user.validation.status;

import com.ecommerce.davivienda.entity.user.UserStatus;
import com.ecommerce.davivienda.exception.user.UserException;
import com.ecommerce.davivienda.service.user.transactional.status.UserStatusTransactionalService;
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
@DisplayName("UserStatusValidationServiceImpl - Tests Unitarios")
class UserStatusValidationServiceImplTest {

    @Mock
    private UserStatusTransactionalService transactionalService;

    @InjectMocks
    private UserStatusValidationServiceImpl validationService;

    private UserStatus mockUserStatus;

    @BeforeEach
    void setUp() {
        mockUserStatus = new UserStatus();
        mockUserStatus.setEstadoUsuarioId(1);
        mockUserStatus.setNombre("Activo");
    }

    @Test
    @DisplayName("findUserStatusByName - UserStatus existe, retorna status")
    void testFindUserStatusByName_Success() {
        when(transactionalService.findUserStatusByName("Activo")).thenReturn(Optional.of(mockUserStatus));

        UserStatus result = validationService.findUserStatusByName("Activo");

        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("Activo");
    }

    @Test
    @DisplayName("findUserStatusByName - UserStatus no encontrado, lanza excepción")
    void testFindUserStatusByName_NotFound_ThrowsException() {
        when(transactionalService.findUserStatusByName("NoExiste")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> validationService.findUserStatusByName("NoExiste"))
                .isInstanceOf(UserException.class);
    }

    @Test
    @DisplayName("findUserStatusById - UserStatus existe por ID")
    void testFindUserStatusById_Success() {
        when(transactionalService.findUserStatusById(1)).thenReturn(Optional.of(mockUserStatus));

        UserStatus result = validationService.findUserStatusById(1);

        assertThat(result).isNotNull();
        assertThat(result.getEstadoUsuarioId()).isEqualTo(1);
    }

    @Test
    @DisplayName("findUserStatusById - UserStatus no encontrado, lanza excepción")
    void testFindUserStatusById_NotFound_ThrowsException() {
        when(transactionalService.findUserStatusById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> validationService.findUserStatusById(999))
                .isInstanceOf(UserException.class);
    }

    @Test
    @DisplayName("getUpdatedUserStatus - RequestId null, retorna current")
    void testGetUpdatedUserStatus_NullId_ReturnsCurrent() {
        UserStatus result = validationService.getUpdatedUserStatus(null, mockUserStatus);

        assertThat(result).isEqualTo(mockUserStatus);
    }

    @Test
    @DisplayName("getUpdatedUserStatus - RequestId válido, retorna nuevo")
    void testGetUpdatedUserStatus_ValidId_ReturnsNew() {
        UserStatus newStatus = new UserStatus();
        newStatus.setEstadoUsuarioId(2);
        newStatus.setNombre("Inactivo");

        when(transactionalService.findUserStatusById(2)).thenReturn(Optional.of(newStatus));

        UserStatus result = validationService.getUpdatedUserStatus(2, mockUserStatus);

        assertThat(result).isEqualTo(newStatus);
        assertThat(result.getEstadoUsuarioId()).isEqualTo(2);
    }
}

