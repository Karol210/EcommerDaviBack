package com.ecommerce.davivienda.service.user.validation.common;

import com.ecommerce.davivienda.exception.user.UserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.ecommerce.davivienda.constants.Constants.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserCommonValidationServiceImpl - Tests Unitarios")
class UserCommonValidationServiceImplTest {

    @InjectMocks
    private UserCommonValidationServiceImpl validationService;

    @Test
    @DisplayName("validatePasswordNotEmpty - Contraseña válida, no lanza excepción")
    void testValidatePasswordNotEmpty_ValidPassword_Success() {
        validationService.validatePasswordNotEmpty("Password123");
    }

    @Test
    @DisplayName("validatePasswordNotEmpty - Contraseña null, lanza excepción")
    void testValidatePasswordNotEmpty_NullPassword_ThrowsException() {
        assertThatThrownBy(() -> validationService.validatePasswordNotEmpty(null))
                .isInstanceOf(UserException.class)
                .hasMessageContaining(ERROR_PASSWORD_EMPTY);
    }

    @Test
    @DisplayName("validatePasswordNotEmpty - Contraseña vacía, lanza excepción")
    void testValidatePasswordNotEmpty_EmptyPassword_ThrowsException() {
        assertThatThrownBy(() -> validationService.validatePasswordNotEmpty(""))
                .isInstanceOf(UserException.class)
                .hasMessageContaining(ERROR_PASSWORD_EMPTY);
    }

    @Test
    @DisplayName("validatePasswordNotEmpty - Contraseña solo espacios, lanza excepción")
    void testValidatePasswordNotEmpty_WhitespacePassword_ThrowsException() {
        assertThatThrownBy(() -> validationService.validatePasswordNotEmpty("   "))
                .isInstanceOf(UserException.class)
                .hasMessageContaining(ERROR_PASSWORD_EMPTY);
    }
}

