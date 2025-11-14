package com.ecommerce.davivienda.service.user.transactional.user;

import com.ecommerce.davivienda.entity.user.Credentials;
import com.ecommerce.davivienda.entity.user.User;
import com.ecommerce.davivienda.repository.user.UserRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests unitarios para UserUserTransactionalServiceImpl.
 *
 * @author Team Ecommerce Davivienda
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserUserTransactionalServiceImpl - Tests Unitarios")
class UserUserTransactionalServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserUserTransactionalServiceImpl transactionalService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setUsuarioId(1);
        Credentials credentials = Credentials.builder()
                .correo("test@example.com")
                .build();
        mockUser.setCredenciales(credentials);
        mockUser.setUsuarioRolId(1);
        mockUser.setNumeroDeDoc("123456789");
    }

    @Test
    @DisplayName("existsByEmail - Email existe, retorna true")
    void testExistsByEmail_Exists_ReturnsTrue() {
        // Arrange
        when(userRepository.existsByCredenciales_Correo("test@example.com")).thenReturn(true);

        // Act
        boolean result = transactionalService.existsByEmail("test@example.com");

        // Assert
        assertThat(result).isTrue();
        verify(userRepository).existsByCredenciales_Correo("test@example.com");
    }

    @Test
    @DisplayName("existsByEmail - Email no existe, retorna false")
    void testExistsByEmail_NotExists_ReturnsFalse() {
        // Arrange
        when(userRepository.existsByCredenciales_Correo("nonexistent@example.com")).thenReturn(false);

        // Act
        boolean result = transactionalService.existsByEmail("nonexistent@example.com");

        // Assert
        assertThat(result).isFalse();
        verify(userRepository).existsByCredenciales_Correo("nonexistent@example.com");
    }

    @Test
    @DisplayName("findUserById - Usuario encontrado")
    void testFindUserById_Found() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));

        // Act
        Optional<User> result = transactionalService.findUserById(1);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getUsuarioId()).isEqualTo(1);
        assertThat(result.get().getCorreo()).isEqualTo("test@example.com");

        verify(userRepository).findById(1);
    }

    @Test
    @DisplayName("findUserById - Usuario no encontrado")
    void testFindUserById_NotFound() {
        // Arrange
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = transactionalService.findUserById(999);

        // Assert
        assertThat(result).isEmpty();
        verify(userRepository).findById(999);
    }

    @Test
    @DisplayName("findUserByEmail - Usuario encontrado")
    void testFindUserByEmail_Found() {
        // Arrange
        when(userRepository.findByCredenciales_Correo("test@example.com"))
                .thenReturn(Optional.of(mockUser));

        // Act
        Optional<User> result = transactionalService.findUserByEmail("test@example.com");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getCorreo()).isEqualTo("test@example.com");

        verify(userRepository).findByCredenciales_Correo("test@example.com");
    }

    @Test
    @DisplayName("findUserByEmail - Usuario no encontrado")
    void testFindUserByEmail_NotFound() {
        // Arrange
        when(userRepository.findByCredenciales_Correo("nonexistent@example.com"))
                .thenReturn(Optional.empty());

        // Act
        Optional<User> result = transactionalService.findUserByEmail("nonexistent@example.com");

        // Assert
        assertThat(result).isEmpty();
        verify(userRepository).findByCredenciales_Correo("nonexistent@example.com");
    }

    @Test
    @DisplayName("findUserByUserRoleId - Usuario encontrado")
    void testFindUserByUserRoleId_Found() {
        // Arrange
        when(userRepository.findByUsuarioRolId(1)).thenReturn(Optional.of(mockUser));

        // Act
        Optional<User> result = transactionalService.findUserByUserRoleId(1);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getUsuarioRolId()).isEqualTo(1);

        verify(userRepository).findByUsuarioRolId(1);
    }

    @Test
    @DisplayName("findByDocumentTypeAndNumber - Usuario encontrado")
    void testFindByDocumentTypeAndNumber_Found() {
        // Arrange
        when(userRepository.findByDocumentType_DocumentoIdAndNumeroDeDoc(1, "123456789"))
                .thenReturn(Optional.of(mockUser));

        // Act
        Optional<User> result = transactionalService.findByDocumentTypeAndNumber(1, "123456789");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getNumeroDeDoc()).isEqualTo("123456789");

        verify(userRepository).findByDocumentType_DocumentoIdAndNumeroDeDoc(1, "123456789");
    }

    @Test
    @DisplayName("findByDocumentTypeAndNumber - Usuario no encontrado")
    void testFindByDocumentTypeAndNumber_NotFound() {
        // Arrange
        when(userRepository.findByDocumentType_DocumentoIdAndNumeroDeDoc(1, "999999999"))
                .thenReturn(Optional.empty());

        // Act
        Optional<User> result = transactionalService.findByDocumentTypeAndNumber(1, "999999999");

        // Assert
        assertThat(result).isEmpty();
        verify(userRepository).findByDocumentType_DocumentoIdAndNumeroDeDoc(1, "999999999");
    }

    @Test
    @DisplayName("findAllUsers - Retorna lista de usuarios")
    void testFindAllUsers_ReturnsUserList() {
        // Arrange
        User user2 = new User();
        user2.setUsuarioId(2);
        Credentials user2Credentials = Credentials.builder()
                .correo("user2@example.com")
                .build();
        user2.setCredenciales(user2Credentials);

        List<User> users = Arrays.asList(mockUser, user2);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<User> result = transactionalService.findAllUsers();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCorreo()).isEqualTo("test@example.com");
        assertThat(result.get(1).getCorreo()).isEqualTo("user2@example.com");

        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("saveUser - Guardar usuario exitosamente")
    void testSaveUser_Success() {
        // Arrange
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Act
        User result = transactionalService.saveUser(mockUser);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsuarioId()).isEqualTo(1);
        assertThat(result.getCorreo()).isEqualTo("test@example.com");

        verify(userRepository).save(mockUser);
    }
}

