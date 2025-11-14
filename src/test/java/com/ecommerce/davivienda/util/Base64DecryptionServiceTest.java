package com.ecommerce.davivienda.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests unitarios para Base64DecryptionService.
 * Cubre encriptación, desencriptación y validación de Base64.
 *
 * @author Team Ecommerce Davivienda
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Base64DecryptionService - Tests Unitarios")
class Base64DecryptionServiceTest {

    @InjectMocks
    private Base64DecryptionService base64DecryptionService;

    private String originalData;
    private String encodedData;

    @BeforeEach
    void setUp() {
        originalData = "Hello, World!";
        encodedData = "SGVsbG8sIFdvcmxkIQ==";
    }

    @Test
    @DisplayName("decrypt - Desencriptar Base64 exitosamente")
    void testDecrypt_Success() {
        // Act
        String decrypted = base64DecryptionService.decrypt(encodedData);

        // Assert
        assertThat(decrypted).isEqualTo(originalData);
    }

    @Test
    @DisplayName("decrypt - Fallar con dato nulo")
    void testDecrypt_NullData_ThrowsException() {
        // Act & Assert
        assertThatThrownBy(() -> base64DecryptionService.decrypt(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El dato a desencriptar no puede estar vacío");
    }

    @Test
    @DisplayName("decrypt - Fallar con dato vacío")
    void testDecrypt_EmptyData_ThrowsException() {
        // Act & Assert
        assertThatThrownBy(() -> base64DecryptionService.decrypt(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El dato a desencriptar no puede estar vacío");
    }

    @Test
    @DisplayName("decrypt - Fallar con dato solo con espacios")
    void testDecrypt_WhitespaceData_ThrowsException() {
        // Act & Assert
        assertThatThrownBy(() -> base64DecryptionService.decrypt("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El dato a desencriptar no puede estar vacío");
    }

    @Test
    @DisplayName("decrypt - Fallar con Base64 inválido")
    void testDecrypt_InvalidBase64_ThrowsException() {
        // Arrange
        String invalidBase64 = "This is not Base64!!!";

        // Act & Assert
        assertThatThrownBy(() -> base64DecryptionService.decrypt(invalidBase64))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El dato proporcionado no es un Base64 válido");
    }

    @Test
    @DisplayName("encrypt - Encriptar string exitosamente")
    void testEncrypt_Success() {
        // Act
        String encrypted = base64DecryptionService.encrypt(originalData);

        // Assert
        assertThat(encrypted).isEqualTo(encodedData);
    }

    @Test
    @DisplayName("encrypt - Fallar con dato nulo")
    void testEncrypt_NullData_ThrowsException() {
        // Act & Assert
        assertThatThrownBy(() -> base64DecryptionService.encrypt(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El dato a encriptar no puede estar vacío");
    }

    @Test
    @DisplayName("encrypt - Fallar con dato vacío")
    void testEncrypt_EmptyData_ThrowsException() {
        // Act & Assert
        assertThatThrownBy(() -> base64DecryptionService.encrypt(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El dato a encriptar no puede estar vacío");
    }

    @Test
    @DisplayName("encrypt - Fallar con dato solo con espacios")
    void testEncrypt_WhitespaceData_ThrowsException() {
        // Act & Assert
        assertThatThrownBy(() -> base64DecryptionService.encrypt("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El dato a encriptar no puede estar vacío");
    }

    @Test
    @DisplayName("encrypt y decrypt - Ciclo completo exitoso")
    void testEncryptDecryptCycle_Success() {
        // Arrange
        String testData = "Test Data 12345!@#$%";

        // Act
        String encrypted = base64DecryptionService.encrypt(testData);
        String decrypted = base64DecryptionService.decrypt(encrypted);

        // Assert
        assertThat(decrypted).isEqualTo(testData);
    }

    @Test
    @DisplayName("isValidBase64 - Validar Base64 válido exitosamente")
    void testIsValidBase64_ValidData_ReturnsTrue() {
        // Act
        boolean isValid = base64DecryptionService.isValidBase64(encodedData);

        // Assert
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("isValidBase64 - Validar Base64 inválido")
    void testIsValidBase64_InvalidData_ReturnsFalse() {
        // Arrange
        String invalidBase64 = "This is not Base64!!!";

        // Act
        boolean isValid = base64DecryptionService.isValidBase64(invalidBase64);

        // Assert
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("isValidBase64 - Validar dato nulo")
    void testIsValidBase64_NullData_ReturnsFalse() {
        // Act
        boolean isValid = base64DecryptionService.isValidBase64(null);

        // Assert
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("isValidBase64 - Validar dato vacío")
    void testIsValidBase64_EmptyData_ReturnsFalse() {
        // Act
        boolean isValid = base64DecryptionService.isValidBase64("");

        // Assert
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("isValidBase64 - Validar dato solo con espacios")
    void testIsValidBase64_WhitespaceData_ReturnsFalse() {
        // Act
        boolean isValid = base64DecryptionService.isValidBase64("   ");

        // Assert
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("decrypt - Desencriptar JSON codificado exitosamente")
    void testDecrypt_JsonEncoded_Success() {
        // Arrange
        String jsonData = "{\"name\":\"John\",\"age\":30}";
        String encodedJson = base64DecryptionService.encrypt(jsonData);

        // Act
        String decryptedJson = base64DecryptionService.decrypt(encodedJson);

        // Assert
        assertThat(decryptedJson).isEqualTo(jsonData);
    }

    @Test
    @DisplayName("decrypt - Desencriptar texto con caracteres especiales")
    void testDecrypt_SpecialCharacters_Success() {
        // Arrange
        String specialChars = "Ñoño's café: ¡123!@#$%^&*()";
        String encoded = base64DecryptionService.encrypt(specialChars);

        // Act
        String decrypted = base64DecryptionService.decrypt(encoded);

        // Assert
        assertThat(decrypted).isEqualTo(specialChars);
    }
}

