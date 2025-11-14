package com.ecommerce.davivienda.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Tests unitarios para JsonUtils.
 * Cubre serialización y deserialización JSON.
 *
 * @author Team Ecommerce Davivienda
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JsonUtils - Tests Unitarios")
class JsonUtilsTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private JsonUtils jsonUtils;

    private TestDto testDto;
    private String jsonString;

    @BeforeEach
    void setUp() {
        testDto = new TestDto("John", 30);
        jsonString = "{\"name\":\"John\",\"age\":30}";
    }

    @Test
    @DisplayName("serializeToJson - Serializar objeto exitosamente")
    void testSerializeToJson_Success() throws JsonProcessingException {
        // Arrange
        when(objectMapper.writeValueAsString(any())).thenReturn(jsonString);

        // Act
        String result = jsonUtils.serializeToJson(testDto);

        // Assert
        assertThat(result).isEqualTo(jsonString);
    }

    @Test
    @DisplayName("serializeToJson - Serializar objeto nulo retorna JSON vacío")
    void testSerializeToJson_NullObject_ReturnsEmptyJson() throws JsonProcessingException {
        // Act
        String result = jsonUtils.serializeToJson(null);

        // Assert
        assertThat(result).isEqualTo("{}");
    }

    @Test
    @DisplayName("serializeToJson - Lanzar excepción cuando falla serialización")
    void testSerializeToJson_JsonProcessingException_ThrowsException() throws JsonProcessingException {
        // Arrange
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("Error"){});

        // Act & Assert
        assertThatThrownBy(() -> jsonUtils.serializeToJson(testDto))
                .isInstanceOf(JsonProcessingException.class);
    }

    @Test
    @DisplayName("deserializeFromJson - Deserializar JSON exitosamente")
    void testDeserializeFromJson_Success() throws JsonProcessingException {
        // Arrange
        when(objectMapper.readValue(anyString(), any(Class.class))).thenReturn(testDto);

        // Act
        TestDto result = jsonUtils.deserializeFromJson(jsonString, TestDto.class);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("John");
        assertThat(result.getAge()).isEqualTo(30);
    }

    @Test
    @DisplayName("deserializeFromJson - Deserializar JSON nulo retorna null")
    void testDeserializeFromJson_NullJson_ReturnsNull() throws JsonProcessingException {
        // Act
        TestDto result = jsonUtils.deserializeFromJson(null, TestDto.class);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("deserializeFromJson - Deserializar JSON vacío retorna null")
    void testDeserializeFromJson_EmptyJson_ReturnsNull() throws JsonProcessingException {
        // Act
        TestDto result = jsonUtils.deserializeFromJson("", TestDto.class);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("deserializeFromJson - Deserializar JSON solo espacios retorna null")
    void testDeserializeFromJson_WhitespaceJson_ReturnsNull() throws JsonProcessingException {
        // Act
        TestDto result = jsonUtils.deserializeFromJson("   ", TestDto.class);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("deserializeFromJson - Lanzar excepción cuando falla deserialización")
    void testDeserializeFromJson_JsonProcessingException_ThrowsException() throws JsonProcessingException {
        // Arrange
        when(objectMapper.readValue(anyString(), any(Class.class)))
                .thenThrow(new JsonProcessingException("Error"){});

        // Act & Assert
        assertThatThrownBy(() -> jsonUtils.deserializeFromJson(jsonString, TestDto.class))
                .isInstanceOf(JsonProcessingException.class);
    }

    /**
     * DTO de prueba para tests.
     */
    private static class TestDto {
        private String name;
        private Integer age;

        public TestDto() {}

        public TestDto(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }
}

