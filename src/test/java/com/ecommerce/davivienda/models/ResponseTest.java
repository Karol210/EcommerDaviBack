package com.ecommerce.davivienda.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitarios para Response.
 * Valida construcción y métodos del modelo de respuesta estándar.
 *
 * @author Team Ecommerce Davivienda
 * @since 1.0.0
 */
@DisplayName("Response - Tests Unitarios")
class ResponseTest {

    @Test
    @DisplayName("Builder - Crear Response exitoso")
    void testBuilder_SuccessResponse() {
        // Arrange & Act
        Response<String> response = Response.<String>builder()
                .failure(false)
                .code(200)
                .message("Operación exitosa")
                .body("Datos de respuesta")
                .timestamp("1234567890")
                .build();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getFailure()).isFalse();
        assertThat(response.getCode()).isEqualTo(200);
        assertThat(response.getMessage()).isEqualTo("Operación exitosa");
        assertThat(response.getBody()).isEqualTo("Datos de respuesta");
        assertThat(response.getTimestamp()).isEqualTo("1234567890");
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    @DisplayName("Builder - Crear Response de error con errorCode")
    void testBuilder_ErrorResponse() {
        // Arrange & Act
        Response<Object> response = Response.builder()
                .failure(true)
                .code(400)
                .message("Error de validación")
                .errorCode("ED-USR-01")
                .timestamp("1234567890")
                .build();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getFailure()).isTrue();
        assertThat(response.getCode()).isEqualTo(400);
        assertThat(response.getMessage()).isEqualTo("Error de validación");
        assertThat(response.getErrorCode()).isEqualTo("ED-USR-01");
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("success - Método estático para crear response exitoso")
    void testSuccess_StaticMethod() {
        // Act
        Response<String> response = Response.success("Operación completada");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getFailure()).isFalse();
        assertThat(response.getCode()).isEqualTo(200);
        assertThat(response.getMessage()).isEqualTo("Operación completada");
        assertThat(response.getBody()).isEqualTo("Operación completada");
    }

    @Test
    @DisplayName("Response - Valores por defecto")
    void testResponse_DefaultValues() {
        // Arrange & Act
        Response<Object> response = Response.builder().build();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getFailure()).isFalse(); // Default es false
        assertThat(response.getCode()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("Response - Con body complejo")
    void testResponse_WithComplexBody() {
        // Arrange
        TestData testData = new TestData("value1", 123);

        // Act
        Response<TestData> response = Response.<TestData>builder()
                .failure(false)
                .code(200)
                .message("Datos obtenidos")
                .body(testData)
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .build();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("value1");
        assertThat(response.getBody().getValue()).isEqualTo(123);
    }

    @Test
    @DisplayName("isFailure - Métodos getter/setter")
    void testIsFailure_GetterSetter() {
        // Arrange
        Response<Object> response = Response.builder()
                .failure(true)
                .build();

        // Act & Assert
        assertThat(response.getFailure()).isTrue();
    }

    /**
     * Clase de test interna para probar body complejo.
     */
    private static class TestData {
        private final String name;
        private final Integer value;

        public TestData(String name, Integer value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public Integer getValue() {
            return value;
        }
    }
}

